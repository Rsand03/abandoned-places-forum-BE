package ee.taltech.iti0302project.app.service.feed;

import ee.taltech.iti0302project.app.dto.feed.CommentDto;
import ee.taltech.iti0302project.app.dto.feed.CreateCommentDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.CommentMapper;
import ee.taltech.iti0302project.app.entity.feed.CommentEntity;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.feed.CommentRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CreateCommentDto commentDto) {
        PostEntity post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new ApplicationException("Post not found"));

        UserEntity user = userRepository.findById(commentDto.getCreatedById())
                .orElseThrow(() -> new ApplicationException("User not found"));

        CommentEntity commentEntity = commentMapper.toEntity(commentDto);
        commentEntity.setPost(post);
        commentEntity.setCreatedBy(user);
        commentEntity.setCreatedAt(LocalDateTime.now());

        CommentEntity savedComment = commentRepository.save(commentEntity);

        log.info("Comment {} uploaded by {}", commentEntity.getBody(), commentEntity.getCreatedBy());

        return commentMapper.toDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Integer postId) {
        List<CommentEntity> comments = commentRepository.findByPostId(postId);
        return commentMapper.toDtoList(comments);
    }
}