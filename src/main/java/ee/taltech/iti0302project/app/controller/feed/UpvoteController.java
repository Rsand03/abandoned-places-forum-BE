package ee.taltech.iti0302project.app.controller.feed;

import ee.taltech.iti0302project.app.dto.feed.UpvoteDto;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.feed.UpvoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feed/upvotes")
@RequiredArgsConstructor
public class UpvoteController {

    private final UpvoteService upvoteService;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<UpvoteDto> createUpvote(@RequestBody UpvoteDto upvoteDto,
                                                  @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);

        upvoteDto.setUserId(userId);

        UpvoteDto createdUpvote = upvoteService.toggleUpvote(upvoteDto);
        return ResponseEntity.ok(createdUpvote);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<UpvoteDto>> getUpvotesByPostId(@PathVariable Long postId) {
        // TODO: implement this endpoint in FE
        List<UpvoteDto> upvotes = upvoteService.getUpvotesByPostId(postId);
        return ResponseEntity.ok(upvotes);
    }
}
