package ee.taltech.iti0302project.test.service.location;

import ee.taltech.iti0302project.app.dto.location.bookmark.BookmarkType;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkCreateDto;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationBookmarkMapper;
import ee.taltech.iti0302project.app.entity.location.LocationBookmarkEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationBookmarkRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.service.location.LocationBookmarkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class LocationBookmarkServiceTest {

    @Mock
    private LocationBookmarkRepository locationBookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationBookmarkMapper locationBookmarkMapper;

    @InjectMocks
    private LocationBookmarkService locationBookmarkService;

    private UUID userId;
    private UUID locationId;
    private LocationEntity locationEntity;
    private LocationBookmarkDto locationBookmarkDto;
    private LocationBookmarkEntity locationBookmarkEntity;
    private LocationBookmarkCreateDto locationBookmarkCreateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        locationId = UUID.randomUUID();
        locationEntity = new LocationEntity();
        locationEntity.setId(locationId);

        locationBookmarkCreateDto = LocationBookmarkCreateDto.builder()
                .createdByUserUuid(userId)
                .locationId(locationId)
                .type(BookmarkType.SUUR_RISK)
                .build();

        locationBookmarkDto = LocationBookmarkDto.builder()
                .type(BookmarkType.SUUR_RISK.getLabel())
                .build();

        locationBookmarkEntity = new LocationBookmarkEntity();
        locationBookmarkEntity.setLocation(locationEntity);
        locationBookmarkEntity.setType(BookmarkType.SUUR_RISK.getLabel());
    }

    @Test
    void testGetLocationBookmarksByUserAndLocation_withLocationId() {
        // Arrange
        when(locationBookmarkRepository.findByCreatedByAndLocation_Id(userId, locationId))
                .thenReturn(List.of(locationBookmarkEntity));
        when(locationBookmarkMapper.toResponseDto(any(LocationBookmarkEntity.class)))
                .thenReturn(locationBookmarkDto);

        // Act
        var result = locationBookmarkService.getLocationBookmarksByUserAndLocation(userId, Optional.of(locationId));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookmarkType.SUUR_RISK.getLabel(), result.getFirst().getType());
        verify(locationBookmarkRepository, times(1)).findByCreatedByAndLocation_Id(userId, locationId);
    }

    @Test
    void testGetLocationBookmarksByUserAndLocation_withoutLocationId() {
        // Arrange
        when(locationBookmarkRepository.findByCreatedBy(userId))
                .thenReturn(List.of(locationBookmarkEntity));
        when(locationBookmarkMapper.toResponseDto(any(LocationBookmarkEntity.class)))
                .thenReturn(locationBookmarkDto);

        // Act
        var result = locationBookmarkService.getLocationBookmarksByUserAndLocation(userId, Optional.empty());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookmarkType.SUUR_RISK.getLabel(), result.getFirst().getType());
        verify(locationBookmarkRepository, times(1)).findByCreatedBy(userId);
    }

    @Test
    void testCreateLocationBookmark_validData() {
        // Arrange
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(locationEntity));
        when(locationRepository.existsById(locationId)).thenReturn(true);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(locationBookmarkMapper.toEntity(any(LocationBookmarkCreateDto.class)))
                .thenReturn(locationBookmarkEntity);
        when(locationBookmarkRepository.save(any(LocationBookmarkEntity.class)))
                .thenReturn(locationBookmarkEntity);
        when(locationBookmarkMapper.toResponseDto(any(LocationBookmarkEntity.class)))
                .thenReturn(locationBookmarkDto);

        // Act
        var result = locationBookmarkService.createLocationBookmark(locationBookmarkCreateDto);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(BookmarkType.SUUR_RISK.getLabel(), result.get().getType());
        verify(locationRepository, times(1)).findById(locationId);
        verify(userRepository, times(1)).existsById(userId);
        verify(locationBookmarkRepository, times(1)).save(any(LocationBookmarkEntity.class));
    }

    @Test
    void testCreateLocationBookmark_userNotFound() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                locationBookmarkService.createLocationBookmark(locationBookmarkCreateDto));
        assertEquals("User not found with ID: " + userId, exception.getMessage());
    }

    @Test
    void testDeleteLocationBookmark_success() {
        // Arrange
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(locationEntity));
        when(locationBookmarkRepository.existsByCreatedByAndLocationAndType(userId, locationEntity, BookmarkType.SUUR_RISK.getLabel()))
                .thenReturn(true);

        // Act
        locationBookmarkService.deleteLocationBookmark(userId, locationId, BookmarkType.SUUR_RISK);

        // Assert
        verify(locationBookmarkRepository, times(1)).deleteByCreatedByAndLocationAndType(userId, locationEntity, BookmarkType.SUUR_RISK.getLabel());
    }

    @Test
    void testDeleteLocationBookmark_notFound() {
        // Arrange
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(locationEntity));
        when(locationBookmarkRepository.existsByCreatedByAndLocationAndType(userId, locationEntity, BookmarkType.SUUR_RISK.getLabel()))
                .thenReturn(false);

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                locationBookmarkService.deleteLocationBookmark(userId, locationId, BookmarkType.SUUR_RISK));
        assertEquals("Bookmark not found for locationId: " + locationId + " and userId: " + userId + " and type: " + BookmarkType.SUUR_RISK, exception.getMessage());
    }
}
