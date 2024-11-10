package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.feed.PostDto;
import ee.taltech.iti0302project.app.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/createPost")
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto createdPost) {
        return ResponseEntity.ok(feedService.createPost(createdPost));
    }

    @GetMapping("")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<PostDto> postDtos = feedService.getAllPosts();
        return ResponseEntity.ok(postDtos);
    }
}
