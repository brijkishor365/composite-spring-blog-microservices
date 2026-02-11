package com.qburst.microservice.post.cache;

import com.qburst.microservice.post.dto.response.post.PostResponse;
import com.qburst.microservice.post.entity.PostEntity;
import com.qburst.microservice.post.exception.post.PostNotFoundException;
import com.qburst.microservice.post.mapper.PostMapper;
import com.qburst.microservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Cacheable(value = "posts", key = "#postId", unless = "#result == null")
    public PostResponse getPostById(Long postId) {

        log.info("Cache MISS -> Fetching postId={} from DB", postId);

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException("Post not found with ID: " + postId)
                );

        return postMapper.toResponse(post);
    }

    @Cacheable(value = "post-slugs", key = "#slug", unless = "#result == null")
    public PostResponse getPostBySlug(String slug) {

        log.info("Cache MISS -> Fetching postSlug={} from DB", slug);

        PostEntity post = postRepository.findBySlug(slug)
                .orElseThrow(() ->
                        new PostNotFoundException("Post not found with slug: " + slug)
                );

        return postMapper.toResponse(post);
    }
}
