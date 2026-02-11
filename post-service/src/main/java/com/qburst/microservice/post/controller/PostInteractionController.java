package com.qburst.microservice.post.controller;

import com.qburst.microservice.post.service.post.PostInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Slf4j
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId) {
        postInteractionService.likePost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId) {
        postInteractionService.unlikePost(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<Long> getPostLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(postInteractionService.getLikeCount(postId));
    }

    @PutMapping("/{postId}/publish")
    public ResponseEntity<Void> publishPost(@PathVariable Long postId) {
        postInteractionService.publishPost(postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/archive")
    public ResponseEntity<Void> archivePost(@PathVariable Long postId) {
        postInteractionService.archivePost(postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/draft")
    public ResponseEntity<Void> draftPost(@PathVariable Long postId) {
        postInteractionService.draftPost(postId);
        return ResponseEntity.ok().build();
    }
}
