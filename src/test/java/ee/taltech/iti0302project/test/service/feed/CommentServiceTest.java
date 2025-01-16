package ee.taltech.iti0302project.test.service.feed;

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
import ee.taltech.iti0302project.app.service.feed.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private CreateCommentDto createCommentDto;
    private CommentEntity commentEntity;
    private PostEntity postEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);

        postEntity = new PostEntity();
        postEntity.setId(1L);

        createCommentDto = CreateCommentDto.builder()
                .postId(1L)
                .body("Test comment")
                .createdById(userId).build();

        commentDto = CommentDto.builder()
                .postId(1L)
                .body("Test comment")
                .createdById(userId).build();

        commentEntity = new CommentEntity();
        commentEntity.setId(1L);
        commentEntity.setPost(postEntity);
        commentEntity.setCreatedBy(userEntity);
        commentEntity.setBody("Test comment");
        commentEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createComment_success() {
        // Given
        given(postRepository.findById(createCommentDto.getPostId())).willReturn(Optional.of(postEntity));
        given(userRepository.findById(createCommentDto.getCreatedById())).willReturn(Optional.of(userEntity));
        given(commentMapper.toEntity(createCommentDto)).willReturn(commentEntity);
        given(commentRepository.save(commentEntity)).willReturn(commentEntity);
        given(commentMapper.toDto(commentEntity)).willReturn(commentDto);

        // When
        CommentDto result = commentService.createComment(createCommentDto);

        // Then
        assertEquals(commentDto, result);
        verify(commentRepository, times(1)).save(commentEntity);
    }

    @Test
    void createComment_postNotFound() {
        // Given
        given(postRepository.findById(createCommentDto.getPostId())).willReturn(Optional.empty());

        // When & Then
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> commentService.createComment(createCommentDto));
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    void createComment_userNotFound() {
        // Given
        given(postRepository.findById(createCommentDto.getPostId())).willReturn(Optional.of(postEntity));
        given(userRepository.findById(createCommentDto.getCreatedById())).willReturn(Optional.empty());

        // When & Then
        ApplicationException exception = assertThrows(ApplicationException.class,
                () -> commentService.createComment(createCommentDto));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getCommentsByPostId_success() {
        // Given
        List<CommentEntity> commentEntities = List.of(commentEntity);
        List<CommentDto> commentDtos = List.of(commentDto);
        given(commentRepository.findByPostId(postEntity.getId().intValue())).willReturn(commentEntities);
        given(commentMapper.toDtoList(commentEntities)).willReturn(commentDtos);

        // When
        List<CommentDto> result = commentService.getCommentsByPostId(postEntity.getId().intValue());

        // Then
        assertEquals(commentDtos, result);
        verify(commentRepository, times(1)).findByPostId(postEntity.getId().intValue());
    }

    @Test
    void getCommentsByPostId_emptyList() {
        // Given
        given(commentRepository.findByPostId(postEntity.getId().intValue())).willReturn(List.of());
        given(commentMapper.toDtoList(List.of())).willReturn(List.of());

        // When
        List<CommentDto> result = commentService.getCommentsByPostId(postEntity.getId().intValue());

        // Then
        assertTrue(result.isEmpty());
    }
}

