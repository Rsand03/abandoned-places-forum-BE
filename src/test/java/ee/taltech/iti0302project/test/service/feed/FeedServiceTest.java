package ee.taltech.iti0302project.test.service.feed;

import ee.taltech.iti0302project.app.criteria.FeedSearchCriteria;
import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.dto.feed.FetchPostsDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.FetchPostsMapper;
import ee.taltech.iti0302project.app.dto.mapper.feed.PostMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.pagination.PageResponse;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.service.feed.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FetchPostsMapper fetchPostsMapper;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    LocationMapper locationMapper;

    @InjectMocks
    private FeedService feedService;

    private CreatePostDto createPostDto;
    private LocationResponseDto locationResponseDto;
    private PostEntity postEntity;
    private UserEntity userEntity;
    private LocationEntity locationEntity;
    private UpvoteEntity upvoteEntity;
    private FetchPostsDto fetchPostsDto;
    private FeedSearchCriteria searchCriteria;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);

        locationEntity = new LocationEntity();
        locationEntity.setId(locationId);
        locationEntity.setPublic(true);

        createPostDto = CreatePostDto.builder()
                .userId(userEntity.getId())
                .locationId(locationEntity.getId())
                .title("Test")
                .body("test").build();

        postEntity = new PostEntity();
        postEntity.setId(1L);
        postEntity.setCreatedBy(userEntity);
        postEntity.setLocation(locationEntity);
        postEntity.setLocation(locationEntity);

        upvoteEntity = new UpvoteEntity();
        upvoteEntity.setPost(postEntity);
        upvoteEntity.setUser(userEntity);

        postEntity.setUpvotes(new ArrayList<>(List.of(upvoteEntity)));

        searchCriteria = new FeedSearchCriteria(
                "Test Title",
                "Test Body",
                UUID.randomUUID(),
                "TestUsername",
                LocalDate.now().minusDays(1),
                LocalDate.now(),
                "id",
                "DESC",
                0,
                10
        );

        fetchPostsDto = FetchPostsDto.builder()
                .id(1L)
                .title("Title")
                .body("Body")
                .createdByUsername("Username")
                .createdAt(LocalDate.now())
                .likeCount(5L)
                .commentCount(10L)
                .hasUpvoted(false)
                .location(new LocationResponseDto())
                .locationId(UUID.randomUUID()).build();


        locationResponseDto = new LocationResponseDto();
    }

    @Test
    void createPost_successfullyCreatesPost() {
        // Given
        given(userRepository.findById(createPostDto.getUserId())).willReturn(Optional.of(userEntity));
        given(locationRepository.findById(createPostDto.getLocationId())).willReturn(Optional.of(locationEntity));
        given(postMapper.toEntity(createPostDto)).willReturn(postEntity);
        given(postRepository.save(postEntity)).willReturn(postEntity);
        given(postMapper.toDto(postEntity)).willReturn(createPostDto);

        // When
        CreatePostDto result = feedService.createPost(createPostDto);

        // Then
        assertEquals(createPostDto, result);
        verify(postRepository, times(1)).save(postEntity);
    }

    @Test
    void createPost_throwsExceptionWhenUserNotFound() {
        // Given
        given(userRepository.findById(createPostDto.getUserId())).willReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> feedService.createPost(createPostDto));
    }

    @Test
    void createPost_throwsExceptionWhenLocationNotPublic() {
        // Given
        locationEntity.setPublic(false);
        given(postMapper.toEntity(createPostDto)).willReturn(postEntity);
        given(userRepository.findById(createPostDto.getUserId())).willReturn(Optional.of(userEntity));
        given(locationRepository.findById(createPostDto.getLocationId())).willReturn(Optional.of(locationEntity));

        // When & Then
        assertThrows(ApplicationException.class, () -> feedService.createPost(createPostDto));
    }

    @Test
    void createPost_throwsExceptionLocationNotAdded() {
        // Given
        createPostDto.setLocationId(null);
        PostEntity postEntityWithNullLocation = new PostEntity();
        postEntityWithNullLocation.setLocation(null);

        given(userRepository.findById(createPostDto.getUserId())).willReturn(Optional.of(userEntity));
        given(postMapper.toEntity(createPostDto)).willReturn(postEntityWithNullLocation);

        // When / Then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            feedService.createPost(createPostDto);
        });
        assertEquals("Invalid location id", exception.getMessage());
    }

    @Test
    void getPostById_returnsPost() {
        // Given
        Long postId = 1L;

        fetchPostsDto = FetchPostsDto.builder()
                .id(1L)
                .title("Sample Title")
                .body("Sample Body")
                .createdByUsername("username")
                .createdAt(LocalDate.now())
                .likeCount(10L)
                .commentCount(5L)
                .hasUpvoted(false)
                .location(new LocationResponseDto())
                .locationId(UUID.randomUUID()).build();

        given(postRepository.findById(postId)).willReturn(Optional.of(postEntity));
        given(fetchPostsMapper.toDto(postEntity)).willReturn(fetchPostsDto);

        // When
        Optional<FetchPostsDto> result = feedService.getPostById(postId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(fetchPostsDto, result.get());
    }

    @Test
    void getPostById_returnsEmptyWhenPostNotFound() {
        // Given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // When
        Optional<FetchPostsDto> result = feedService.getPostById(postId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void hasUserUpvotedPost_returnsTrueWhenUserHasUpvoted() {
        // Given
        Long postId = 1L;
        UpvoteEntity upvoteEntity2 = new UpvoteEntity();
        upvoteEntity2.setUser(userEntity);
        postEntity.setUpvotes(List.of(upvoteEntity2));
        given(postRepository.findById(postId)).willReturn(Optional.of(postEntity));

        // When
        boolean result = feedService.hasUserUpvotedPost(postId, userEntity.getId());

        // Then
        assertTrue(result);
    }

    @Test
    void hasUserUpvotedPost_returnsFalseWhenUserHasNotUpvoted() {
        // Given
        Long postId = 1L;
        UUID userId = UUID.randomUUID();
        given(postRepository.findById(postId)).willReturn(Optional.of(postEntity));

        // When
        boolean result = feedService.hasUserUpvotedPost(postId, userId);

        // Then
        assertFalse(result);
    }

    @Test
    void findPosts_success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<PostEntity> postPage = mock(Page.class);
        List<PostEntity> postEntities = List.of(postEntity);

        given(postRepository.findAll(any(Specification.class), eq(pageable))).willReturn(postPage);
        given(postPage.getContent()).willReturn(postEntities);
        given(fetchPostsMapper.toDto(postEntity)).willReturn(fetchPostsDto);
        given(locationRepository.findById(postEntity.getLocation().getId())).willReturn(Optional.of(locationEntity));
        given(postRepository.findById(postEntity.getId())).willReturn(Optional.of(postEntity));
        given(locationMapper.toResponseDto(locationEntity)).willReturn(locationResponseDto);
        given(postPage.getTotalElements()).willReturn(1L);

        // When
        PageResponse<FetchPostsDto> result = feedService.findPosts(searchCriteria, userEntity.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals(fetchPostsDto, result.content().getFirst());
        verify(postRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findPosts_noResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<PostEntity> postPage = mock(Page.class);
        List<PostEntity> postEntities = List.of();

        given(postPage.getContent()).willReturn(postEntities);
        given(postRepository.findAll(any(Specification.class), eq(pageable))).willReturn(postPage);

        // When
        PageResponse<FetchPostsDto> result = feedService.findPosts(searchCriteria, userEntity.getId());

        // Then
        assertNotNull(result);
        assertEquals(0, result.totalElements());
        assertTrue(result.content().isEmpty());

        ArgumentCaptor<Specification<PostEntity>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(postRepository, times(1)).findAll(specCaptor.capture(), eq(pageable));

        Specification<PostEntity> capturedSpec = specCaptor.getValue();
        assertNotNull(capturedSpec);
    }

    @Test
    void findPosts_locationNotFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<PostEntity> postPage = mock(Page.class);
        List<PostEntity> postEntities = List.of(postEntity);

        given(postRepository.findAll(any(Specification.class), eq(pageable))).willReturn(postPage);
        given(postPage.getContent()).willReturn(postEntities);
        given(locationRepository.findById(postEntity.getLocation().getId())).willReturn(Optional.of(locationEntity));

        given(locationRepository.findById(postEntity.getLocation().getId())).willReturn(Optional.empty());

        // When & Then
        UUID uuid = userEntity.getId();
        assertThrows(ApplicationException.class, () -> {
            feedService.findPosts(searchCriteria, uuid);
        });

        verify(postRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}

