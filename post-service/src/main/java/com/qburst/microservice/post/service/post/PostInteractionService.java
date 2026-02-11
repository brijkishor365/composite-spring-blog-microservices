package com.qburst.microservice.post.service.post;

public interface PostInteractionService {

    void likePost(Long postId);

    void unlikePost(Long postId);

    Long getLikeCount(Long postId);

    void publishPost(Long postId);

    void archivePost(Long postId);

    void draftPost(Long postId);
}

