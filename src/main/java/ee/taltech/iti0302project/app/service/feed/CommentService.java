package ee.taltech.iti0302project.app.service.feed;

import ee.taltech.iti0302project.app.dto.feed.CommentDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.CommentMapper;
import ee.taltech.iti0302project.app.entity.feed.CommentEntity;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.feed.CommentRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentDto createComment(Long postId, String body, UUID userId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setBody(body);
        commentEntity.setPost(post);
        commentEntity.setCreatedBy(user);
        commentEntity.setCreatedAt(LocalDateTime.now());

        CommentEntity savedComment = commentRepository.save(commentEntity);

        return commentMapper.toDto(savedComment);
    }

    public List<CommentDto> getCommentsByPostId(Integer postId) {
        List<CommentEntity> comments = commentRepository.findByPostId(postId);
        return commentMapper.toDtoList(comments);
    }
}