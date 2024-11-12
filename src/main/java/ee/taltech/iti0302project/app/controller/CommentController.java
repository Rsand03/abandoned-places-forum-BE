package ee.taltech.iti0302project.app.controller;

import com.fasterxml.jackson.databind.JsonNode;
import ee.taltech.iti0302project.app.dto.feed.CommentDto;
import ee.taltech.iti0302project.app.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long postId,
            @RequestBody() JsonNode body,
            @RequestHeader("User-ID") UUID userId
    ) {
        String comment = body.get("comment").asText();
        CommentDto commentDto = commentService.createComment(postId, comment, userId);
        return ResponseEntity.ok(commentDto);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Integer postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
}
