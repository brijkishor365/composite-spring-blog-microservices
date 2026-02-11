package com.qburst.microservice.post.service.post.Impl;

import com.qburst.microservice.post.client.AuthUserClient;
import com.qburst.microservice.post.dto.common.UserDTO;
import com.qburst.microservice.post.dto.request.post.PostRequest;
import com.qburst.microservice.post.dto.response.post.PostResponse;
import com.qburst.microservice.post.entity.PostEntity;
import com.qburst.microservice.post.entity.CategoryEntity;
import com.qburst.microservice.post.exception.post.PostNotFoundException;
import com.qburst.microservice.post.exception.category.CategoryNotFoundException;
import com.qburst.microservice.post.exception.user.UserNotFoundException;
import com.qburst.microservice.post.mapper.PostMapper;
import com.qburst.microservice.post.repository.PostRepository;
import com.qburst.microservice.post.repository.CategoryRepository;
import com.qburst.microservice.post.service.cache.RedisService;
import com.qburst.microservice.post.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    //    private final UserRepository userRepository;
    private final AuthUserClient authUserClient;
    private final CategoryRepository categoryRepository;
    private final PostMapper postMapper;
    private final RedisService redisService;

    @Transactional
    @Override
    @CachePut(value = "posts", key = "#result.id()")
    public PostResponse createPost(PostRequest request) {

        Long authorId = request.authorId();

        // Validate user exists
        UserDTO author = authUserClient.getUserById(authorId);

        if (author == null) {
            throw new UserNotFoundException("Author not found with ID: " + authorId);
        }

        log.info("Creating post for authorId={}, username={}", authorId, author.username());

        // Validate category
        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category ID '" + request.categoryId() + "' does not exist"
                        )
                );

        // Generate slug
        String slug = generateUniqueSlug(request.title());

        // Build post entity
        PostEntity post = PostEntity.builder()
                .title(request.title())
                .content(request.content())
                .summary(request.summary())
                .imageUrl(request.imageUrl())
                .isPublished(request.published())
                .tags(request.tags())
                .slug(slug)
                .authorId(authorId)
                .category(category)
                .build();

        // Save post
        PostEntity savedPost = postRepository.save(post);

        // Map to response and return
        return mapToResponse(savedPost, author);
    }

    private PostResponse mapToResponse(PostEntity post, UserDTO author) {

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getSlug(),
                post.getContent(),
                post.getSummary(),
                post.getImageUrl(),
                post.getCategory() != null ? post.getCategory().getName() : "Uncategorized",
                author.fullName(),
                post.getTags(),
                post.getViewCount(),
                post.getIsPublished(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replace(" ", "-");

        String finalSlug;
        do {
            String suffix = UUID.randomUUID().toString().substring(0, 5);
            finalSlug = baseSlug + "-" + suffix;
            // Only loop if the slug is taken by an ACTIVE post
        } while (postRepository.existsBySlug(finalSlug));

        return finalSlug;
    }

    @Cacheable(
            value = "post:by-slug",
            key = "#slug",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public PostResponse getPostBySlug(String slug) {

        PostEntity post = postRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new PostNotFoundException("Post not found with slug: " + slug)
                );

        Long authorId = post.getAuthorId();

        // Get user data from redis, if not available in redis then pull from auth service and set in redis
        String userKey = "post:user:" + authorId;
        UserDTO author = redisService.get(userKey, UserDTO.class);
        if (author == null) {
            // Fetch author details once from auth service

            author = authUserClient.getUserById(authorId);
            if (author == null) {
                throw new UserNotFoundException("Author not found with ID: " + authorId);
            }
            redisService.set(userKey, author, 10, TimeUnit.MINUTES);
        }

//        // Fetch author details ONCE
//        UserDTO author = authUserClient.getUserById(authorId);
//        if (author == null) {
//            throw new UserNotFoundException("Author not found with ID: " + authorId);
//        }

        return mapToResponse(post, author);
    }

    @Cacheable(
            value = "post:published",
            key = "#pageable.pageNumber",
            condition = "#pageable.pageNumber == 0"
    )
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPublishedPosts(Pageable pageable) {
        Page<PostEntity> blogs = postRepository.findByIsPublishedTrueOrderByCreatedAtDesc(pageable);

        return blogs.map(postMapper::toResponse);
    }

    @Override
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional
    public PostResponse updatePost(String slug, PostRequest request) {
        PostEntity existingPost = postRepository.findBySlug(slug)
                .orElseThrow(() -> new PostNotFoundException("Post not found with slug: " + slug));

        // Security Check: Only the author can update
//        Long currentUserId = SecurityUtils.getCurrentUserId();
//        if (!existingPost.getAuthor().getId().equals(currentUserId)) {
//            throw new UnauthorizedException("You are not authorized to update this post.");
//        }

        // Category Update: If ID changed, fetch the new entity
        if (!existingPost.getCategory().getId().equals(request.categoryId())) {
            CategoryEntity newCategory = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

            // set new category
            existingPost.setCategory(newCategory);
        }

        // Note: ignored categoryId in Mapper to avoid conflicts
        postMapper.updateEntityFromDto(request, existingPost);

        PostEntity updatedPost = postRepository.save(existingPost);
        return postMapper.toResponse(updatedPost);
    }

    @Transactional
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePostBySlug(String slug) {
        PostEntity post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new PostNotFoundException("Post not found with slug: " + slug));

        // Do not delete other users post
//        PostEntity post = postRepository.findBySlugAndAuthorId(slug, currentUserId)
//                .orElseThrow(() -> new UnauthorizedException("You do not own this post, or it doesn't exist."));

        // Perform soft delete
        postRepository.delete(post);
    }

    @Cacheable(
            value = "post:by-id",
            key = "#blogId",
            unless = "#result == null"
    )
    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long blogId) {

        PostEntity post = postRepository.findById(blogId)
                .orElseThrow(() ->
                        new PostNotFoundException("Post not found with ID: " + blogId)
                );

        Long authorId = post.getAuthorId();

        UserDTO author = redisService.get("post:user", UserDTO.class);
        if (author == null) {
            // Fetch author details once from auth service

            author = authUserClient.getUserById(authorId);
            redisService.set("post:user", author, 10, TimeUnit.MINUTES);
        }

        if (author == null) {
            throw new UserNotFoundException("Author not found with ID: " + authorId);
        }

        return mapToResponse(post, author);
    }

    @Override
    public Page<PostResponse> searchPost(Pageable pageable, String keywords) {
        Page<PostEntity> posts = postRepository.findByTitleContainingOrContentContaining(keywords, keywords, pageable);

        return posts.map(postMapper::toResponse);
    }

    @Cacheable(
            value = "post:by-category",
            key = "#categoryId + ':' + #pageable.pageNumber",
            condition = "#pageable.pageNumber == 0"
    )
    @Override
    public Page<PostResponse> getPostByCategory(Pageable pageable, Long categoryId) {
        Page<PostEntity> postRepositoryByCategoryId = postRepository.findByCategoryId(categoryId, pageable);

        return postRepositoryByCategoryId.map(postMapper::toResponse);
    }

    @Override
    public Page<PostResponse> getPostByUser(Pageable pageable, Long userId) {
        Page<PostEntity> authorPosts = postRepository.findByAuthorId(userId, pageable);

        if (authorPosts.isEmpty()) {
            throw new PostNotFoundException("Post not found for User: " + userId);
        }

        return authorPosts.map(postMapper::toResponse);
    }

    public Flux<String> getMessages() {
        return Flux.just("Message 1", "Message 2", "Message 3")
                .delayElements(Duration.ofSeconds(3));
    }
}