package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.feed.PostDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.PostMapper;
import ee.taltech.iti0302project.app.entity.UserEntity;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FeedService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostDto createPost(PostDto createdPost) {
        PostEntity entity = postMapper.toEntity(createdPost);

        entity.setCreatedAt(generateRandomTimestamp());

        postRepository.save(entity);

        return postMapper.toDto(entity);
    }

    public List<PostDto> getAllPosts() {
        List<PostEntity> posts = postRepository.findAll();
        return posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    private Timestamp generateRandomTimestamp() {
        long randomTime = System.currentTimeMillis() - (long) (Math.random() * 1000000000);
        return new Timestamp(randomTime);
    }
}
