package ee.taltech.iti0302project.app.controller.feed;

import ee.taltech.iti0302project.app.dto.feed.CommentDto;
import ee.taltech.iti0302project.app.dto.feed.CreateCommentDto;
import ee.taltech.iti0302project.app.service.feed.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comments management APIs")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Create a new comment",
            description = "Allows users to create a new comment for a specific post"
    )
    @ApiResponse(responseCode = "200", description = "Comment created successfully")
    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @Valid @RequestBody CreateCommentDto commentDto
    ) {
        CommentDto response = commentService.createComment(commentDto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Retrieve comments for a post",
            description = "Fetches all comments associated with the given post ID"
    )
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Integer postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
}
