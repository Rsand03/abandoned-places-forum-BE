package ee.taltech.iti0302project.test.service.feed;

import ee.taltech.iti0302project.app.dto.feed.UpvoteDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.UpvoteMapper;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import ee.taltech.iti0302project.app.repository.feed.UpvoteRepository;
import ee.taltech.iti0302project.app.service.feed.UpvoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpvoteServiceTest {

    @Mock
    private UpvoteRepository upvoteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UpvoteMapper upvoteMapper;

    @InjectMocks
    private UpvoteService upvoteService;

    private UpvoteDto upvoteDto;
    private UpvoteEntity upvoteEntity;
    private UserEntity userEntity;
    private PostEntity postEntity;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);

        postEntity = new PostEntity();

        PostEntity postEntity = new PostEntity();
        postEntity.setId(1L);

        upvoteDto = UpvoteDto.builder()
                .postId(1L)
                .userId(userEntity.getId())
                .build();

        upvoteEntity = new UpvoteEntity();
        upvoteEntity.setId(1L);
        upvoteEntity.setPost(postEntity);
        upvoteEntity.setUser(userEntity);
    }

    @Test
    void toggleUpvote_create_success() {
        // Given
        given(upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())).willReturn(false);
        given(userRepository.findById(upvoteDto.getUserId())).willReturn(Optional.of(userEntity));
        given(postRepository.findById(upvoteDto.getPostId())).willReturn(Optional.of(postEntity));
        given(upvoteMapper.toEntity(upvoteDto)).willReturn(upvoteEntity);
        given(upvoteRepository.save(upvoteEntity)).willReturn(upvoteEntity);
        given(upvoteMapper.toDto(upvoteEntity)).willReturn(upvoteDto);

        // When
        UpvoteDto result = upvoteService.toggleUpvote(upvoteDto);

        // Then
        assertEquals(upvoteDto, result);
        verify(upvoteRepository, times(1)).save(upvoteEntity);
    }

    @Test
    void toggleUpvote_delete_success() {
        // Given
        given(upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())).willReturn(true);
        given(upvoteRepository.findByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())).willReturn(Optional.of(upvoteEntity));

        // When
        UpvoteDto result = upvoteService.toggleUpvote(upvoteDto);

        // Then
        assertEquals(upvoteDto, result);
        verify(upvoteRepository, times(1)).delete(upvoteEntity);
    }

    @Test
    void toggleUpvote_alreadyUpvoted() {
        // Given
        given(upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())).willReturn(true);
        given(upvoteRepository.findByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())).willReturn(Optional.of(upvoteEntity));

        // When
        UpvoteDto result = upvoteService.toggleUpvote(upvoteDto);

        // Then
        assertEquals(upvoteDto, result);
        verify(upvoteRepository, times(1)).delete(upvoteEntity);
    }

    @Test
    void getUpvotesByPostId_success() {
        // Given
        given(upvoteRepository.findByPostId(1L)).willReturn(List.of(upvoteEntity));
        given(upvoteMapper.toDtoList(List.of(upvoteEntity))).willReturn(List.of(upvoteDto));

        // When
        List<UpvoteDto> result = upvoteService.getUpvotesByPostId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(upvoteDto, result.getFirst());
        verify(upvoteRepository, times(1)).findByPostId(1L);
    }

    @Test
    void getUpvotesByPostId_emptyList() {
        // Given
        given(upvoteRepository.findByPostId(1L)).willReturn(List.of());
        given(upvoteMapper.toDtoList(List.of())).willReturn(List.of());

        // When
        List<UpvoteDto> result = upvoteService.getUpvotesByPostId(1L);

        // Then
        assertTrue(result.isEmpty());
    }
}
