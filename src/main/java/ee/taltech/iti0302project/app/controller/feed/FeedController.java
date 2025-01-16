package ee.taltech.iti0302project.app.controller.feed;

import ee.taltech.iti0302project.app.criteria.FeedSearchCriteria;
import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.dto.feed.FetchPostsDto;
import ee.taltech.iti0302project.app.pagination.PageResponse;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.feed.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@Tag(name = "Feed", description = "Feed management APIs")
public class FeedController {

    private final FeedService feedService;
    private final JwtService jwtService;

    @Operation(
            summary = "Create a new post",
            description = "Allows users to create a new post in the feed"
    )
    @ApiResponse(responseCode = "200", description = "Post created successfully")
    @PostMapping("/createPost")
    public ResponseEntity<CreatePostDto> createPost(@Valid @RequestBody CreatePostDto createdPost) {
        return ResponseEntity.ok(feedService.createPost(createdPost));
    }

    @Operation(
            summary = "Retrieve posts based on search criteria",
            description = "Fetches posts from the feed based on search criteria and the user ID extracted from the authorization header"
    )
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    @GetMapping("")
    public PageResponse<FetchPostsDto> getPosts(
            @Valid @ModelAttribute FeedSearchCriteria criteria,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);

        return feedService.findPosts(criteria, userId);
    }

    @Operation(
            summary = "Retrieve a specific post by ID",
            description = "Fetches a post by its unique ID"
    )
    @ApiResponse(responseCode = "200", description = "Post retrieved successfully")
    @GetMapping("/{postId}")
    public ResponseEntity<FetchPostsDto> getPostById(
            @PathVariable Long postId) {
        return feedService.getPostById(postId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
