package ee.taltech.iti0302project.app.controller.feed;

import ee.taltech.iti0302project.app.dto.feed.CreateUpvoteDto;
import ee.taltech.iti0302project.app.dto.feed.UpvoteResponseDto;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.feed.UpvoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed/upvotes")
@RequiredArgsConstructor
@Tag(name = "Upvotes", description = "Upvotes management APIs")
public class UpvoteController {

    private final UpvoteService upvoteService;
    private final JwtService jwtService;

    @Operation(
            summary = "Ttoggle an upvote for a post",
            description = "Allows users to upvote or remove an upvote for a specific post."
    )
    @ApiResponse(responseCode = "200", description = "Upvote created or toggled successfully")
    @PostMapping
    public ResponseEntity<UpvoteResponseDto> createUpvote(@Valid @RequestBody CreateUpvoteDto upvoteDto,
                                                          @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);

        upvoteDto.setUserId(userId);

        UpvoteResponseDto createdUpvote = upvoteService.toggleUpvote(upvoteDto);
        return ResponseEntity.ok(createdUpvote);
    }

    @Operation(
            summary = "Retrieve upvotes for a specific post",
            description = "Fetches all upvotes associated with a given post ID."
    )
    @ApiResponse(responseCode = "200", description = "Upvotes retrieved successfully")
    @GetMapping("/{postId}")
    public ResponseEntity<List<UpvoteResponseDto>> getUpvotesByPostId(@PathVariable Long postId) {
        List<UpvoteResponseDto> upvotes = upvoteService.getUpvotesByPostId(postId);
        return ResponseEntity.ok(upvotes);
    }
}
