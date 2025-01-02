package ee.taltech.iti0302project.test.service.feed;

import ee.taltech.iti0302project.app.dto.feed.UpvoteDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.UpvoteMapper;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.repository.feed.UpvoteRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
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
    private PostRepository postRepository;
    @Mock
    private UpvoteMapper upvoteMapper;

    @InjectMocks
    private UpvoteService upvoteService;

    private UpvoteDto upvoteDto;
    private UpvoteEntity upvoteEntity;
    private PostEntity postEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);

        postEntity = new PostEntity();
        postEntity.setId(1L);

        upvoteDto = new UpvoteDto();
        upvoteDto.setPostId(1L);
        upvoteDto.setUserId(userEntity.getId());

        upvoteEntity = new UpvoteEntity();
        upvoteEntity.setId(1L);
        upvoteEntity.setPostId(postEntity.getId());
        upvoteEntity.setUserId(userEntity.getId());
    }

    @Test
    void toggleUpvote_create_success() {
        // Given
        given(upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())).willReturn(false);
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
    void createUpvote_success() {
        // Given
        given(upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())).willReturn(false);
        given(upvoteMapper.toEntity(upvoteDto)).willReturn(upvoteEntity);
        given(upvoteRepository.save(upvoteEntity)).willReturn(upvoteEntity);
        given(upvoteMapper.toDto(upvoteEntity)).willReturn(upvoteDto);

        // When
        UpvoteDto result = upvoteService.createUpvote(upvoteDto);

        // Then
        assertEquals(upvoteDto, result);
        verify(upvoteRepository, times(1)).save(upvoteEntity);
    }

    @Test
    void createUpvote_alreadyUpvoted() {
        // Given
        given(upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())).willReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> upvoteService.createUpvote(upvoteDto));
        assertEquals("User has already upvoted this post.", exception.getMessage());
    }

    @Test
    void deleteUpvote_success() {
        // Given
        doNothing().when(upvoteRepository).deleteById(1L);

        // When
        upvoteService.deleteUpvote(1L);

        // Then
        verify(upvoteRepository, times(1)).deleteById(1L);
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
        assertEquals(upvoteDto, result.get(0));
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

    @Test
    void getUpvoteById_success() {
        // Given
        given(upvoteRepository.findById(1L)).willReturn(Optional.of(upvoteEntity));
        given(upvoteMapper.toDto(upvoteEntity)).willReturn(upvoteDto);

        // When
        UpvoteDto result = upvoteService.getUpvoteById(1L);

        // Then
        assertEquals(upvoteDto, result);
        verify(upvoteRepository, times(1)).findById(1L);
    }

    @Test
    void getUpvoteById_notFound() {
        // Given
        given(upvoteRepository.findById(1L)).willReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> upvoteService.getUpvoteById(1L));
        assertEquals("Upvote not found with id: 1", exception.getMessage());
    }

    @Test
    void getAllUpvotes_success() {
        // Given
        List<UpvoteEntity> upvoteEntities = List.of(upvoteEntity);
        List<UpvoteDto> upvoteDtos = List.of(upvoteDto);

        given(upvoteRepository.findAll()).willReturn(upvoteEntities);
        given(upvoteMapper.toDtoList(upvoteEntities)).willReturn(upvoteDtos);

        // When
        List<UpvoteDto> result = upvoteService.getAllUpvotes();

        // Then
        assertEquals(upvoteDtos, result);
        verify(upvoteRepository, times(1)).findAll();
        verify(upvoteMapper, times(1)).toDtoList(upvoteEntities);
    }
}
