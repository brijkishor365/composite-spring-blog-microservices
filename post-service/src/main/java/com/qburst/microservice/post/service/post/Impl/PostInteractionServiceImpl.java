package com.qburst.microservice.post.service.post.Impl;

import com.qburst.microservice.post.entity.PostEntity;
import com.qburst.microservice.post.entity.PostStatus;
import com.qburst.microservice.post.exception.post.PostNotFoundException;
import com.qburst.microservice.post.repository.PostRepository;
import com.qburst.microservice.post.service.post.PostInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostInteractionServiceImpl implements PostInteractionService {

    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Keys
    private static final String POST_LIKE_COUNT = "post:likes:";
    private static final String POST_LIKED_USERS = "post:liked:users:";

    @Override
    @Transactional
    public void likePost(Long postId) {

        Long userId = getCurrentUserId();

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        String userLikeKey = POST_LIKED_USERS + postId;

        Boolean alreadyLiked = redisTemplate.opsForSet().isMember(userLikeKey, userId);
        if (Boolean.TRUE.equals(alreadyLiked)) {
            log.debug("User {} already liked post {}", userId, postId);
            return;
        }

        redisTemplate.opsForSet().add(userLikeKey, userId);
        redisTemplate.opsForValue().increment(POST_LIKE_COUNT + postId);

        log.info("User {} liked post {}", userId, postId);
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {

        Long userId = getCurrentUserId();

        String userLikeKey = POST_LIKED_USERS + postId;

        Boolean liked = redisTemplate.opsForSet().isMember(userLikeKey, userId);
        if (!Boolean.TRUE.equals(liked)) {
            return;
        }

        redisTemplate.opsForSet().remove(userLikeKey, userId);
        redisTemplate.opsForValue().decrement(POST_LIKE_COUNT + postId);

        log.info("User {} unliked post {}", userId, postId);
    }

    @Override
    public Long getLikeCount(Long postId) {

        Object value = redisTemplate.opsForValue().get(POST_LIKE_COUNT + postId);
        if (value != null) {
            return (Long) value;
        }

        // Fallback to DB (optional)
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        Long count = post.getLikeCount();
        redisTemplate.opsForValue().set(POST_LIKE_COUNT + postId, count);

        return count;
    }

    @Override
    @Transactional
    public void publishPost(Long postId) {
        updateStatus(postId, PostStatus.PUBLISHED);
    }

    @Override
    @Transactional
    public void archivePost(Long postId) {
        updateStatus(postId, PostStatus.ARCHIVED);
    }

    @Override
    @Transactional
    public void draftPost(Long postId) {
        updateStatus(postId, PostStatus.DRAFT);
    }

    private void updateStatus(Long postId, PostStatus status) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        post.setStatus(status);
        postRepository.save(post);

        // Evict caches
        redisTemplate.delete("post:by-id:" + postId);
        redisTemplate.delete("post:by-slug:" + post.getSlug());

        log.info("Post {} status updated to {}", postId, status);
    }

    private Long getCurrentUserId() {
        // Replace with SecurityContext logic
        return 1L;
    }
}
