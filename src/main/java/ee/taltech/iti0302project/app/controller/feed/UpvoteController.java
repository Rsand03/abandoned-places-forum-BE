package ee.taltech.iti0302project.app.controller.feed;

import ee.taltech.iti0302project.app.dto.feed.UpvoteDto;
import ee.taltech.iti0302project.app.service.feed.UpvoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed/upvotes")
@RequiredArgsConstructor
public class UpvoteController {

    private final UpvoteService upvoteService;

    @GetMapping
    public List<UpvoteDto> getAllUpvotes() {
        return upvoteService.getAllUpvotes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UpvoteDto> getUpvoteById(@PathVariable Integer id) {
        return ResponseEntity.ok(upvoteService.getUpvoteById(id));
    }

    @PostMapping
    public ResponseEntity<UpvoteDto> createUpvote(@RequestBody UpvoteDto upvoteDTO) {

        if (upvoteDTO.getUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(upvoteDTO);
        }

        UpvoteDto createdUpvote = upvoteService.toggleUpvote(upvoteDTO);
        return ResponseEntity.ok(createdUpvote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUpvote(@PathVariable Integer id) {
        upvoteService.deleteUpvote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byPost/{postId}")
    public ResponseEntity<List<UpvoteDto>> getUpvotesByPostId(@PathVariable Integer postId) {
        List<UpvoteDto> upvotes = upvoteService.getUpvotesByPostId(postId);
        return ResponseEntity.ok(upvotes);
    }
}
