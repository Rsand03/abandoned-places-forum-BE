package ee.taltech.iti0302project.app.service.feed;

import ee.taltech.iti0302project.app.dto.feed.FetchPostsDto;
import ee.taltech.iti0302project.app.dto.feed.PostDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.FetchPostsMapper;
import ee.taltech.iti0302project.app.dto.mapper.feed.PostMapper;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FetchPostsMapper fetchPostsMapper;
    private final UserRepository userRepository;

    public PostDto createPost(PostDto createdPost) {
        UserEntity user = userRepository.findById(createdPost.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostEntity entity = postMapper.toEntity(createdPost);

        entity.setCreatedBy(user);

        entity.setCreatedAt(generateRandomTimestamp());

        postRepository.save(entity);

        return postMapper.toDto(entity);
    }

    public List<FetchPostsDto> getAllPosts() {
        List<PostEntity> posts = postRepository.findAll();
        return posts.stream()
                .map(fetchPostsMapper::toDto)
                .collect(Collectors.toList());
    }

    private Timestamp generateRandomTimestamp() {
        long randomTime = System.currentTimeMillis() - (long) (Math.random() * 1000000000);
        return new Timestamp(randomTime);
    }
}
