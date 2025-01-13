package ee.taltech.iti0302project.app.controller.feed;

import ee.taltech.iti0302project.app.criteria.FeedSearchCriteria;
import ee.taltech.iti0302project.app.dto.feed.FetchPostsDto;
import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.pagination.PageResponse;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.feed.FeedService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final SecretKey key;
    private final JwtService jwtService;

    @PostMapping("/createPost")
    public ResponseEntity<CreatePostDto> createPost(@RequestBody CreatePostDto createdPost) {
        return ResponseEntity.ok(feedService.createPost(createdPost));
    }

    @GetMapping("")
    public PageResponse<FetchPostsDto> getPosts(
            @Valid @ModelAttribute FeedSearchCriteria criteria,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);

        return feedService.findPosts(criteria, userId);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<FetchPostsDto> getPostById(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authHeader) {
        // TODO: decide what to do with authHeader here
        return feedService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
