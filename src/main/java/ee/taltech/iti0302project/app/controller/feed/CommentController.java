package ee.taltech.iti0302project.app.controller.feed;

import ee.taltech.iti0302project.app.dto.feed.CommentDto;
import ee.taltech.iti0302project.app.service.feed.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @RequestBody CommentDto commentDto
    ) {
        CommentDto response = commentService.createComment(commentDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Integer postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }
}
