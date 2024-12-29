package ee.taltech.iti0302project.test.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationCategoryMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationConditionMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationStatusMapper;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationCategoryRepository;
import ee.taltech.iti0302project.app.repository.location.LocationConditionRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.repository.location.LocationStatusRepository;
import ee.taltech.iti0302project.app.service.location.LocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationCategoryRepository locationCategoryRepository;
    @Mock
    private LocationConditionRepository locationConditionRepository;
    @Mock
    private LocationStatusRepository locationStatusRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationMapper locationMapper;
    @Mock
    private LocationCategoryMapper categoryMapper;
    @Mock
    private LocationConditionMapper conditionMapper;
    @Mock
    private LocationStatusMapper statusMapper;

    @InjectMocks
    private LocationService locationService;


    @Test
    void getLocationAttributes() {

    }

    @Test
    void getFilteredLocations() {
    }

    @Test
    void deleteLocationByUuid() {
        // Arrange
        UUID locationId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        LocationEntity mockEntity = new LocationEntity();
        mockEntity.setId(locationId);
        mockEntity.setCreatedBy(createdBy);
        mockEntity.setPublic(false);

        LocationResponseDto responseDto = new LocationResponseDto();
        responseDto.setId(locationId);
        responseDto.setName("Test Location");

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(mockEntity));
        when(locationMapper.toResponseDto(mockEntity)).thenReturn(responseDto);

        // Act
        Optional<LocationResponseDto> result = locationService.deleteLocationByUuid(locationId, createdBy);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(locationId, result.get().getId());
    }

    @Test
    void deleteLocationByUuid_differentCreatedBy() {
        // Arrange
        UUID locationId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        LocationEntity mockEntity = new LocationEntity();
        mockEntity.setId(locationId);
        mockEntity.setCreatedBy(UUID.randomUUID()); // Different createdBy
        mockEntity.setPublic(false);

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(mockEntity));

        // Act
        Optional<LocationResponseDto> result = locationService.deleteLocationByUuid(locationId, createdBy);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void deleteLocationByUuid_isPublicLocation() {
        // Arrange
        UUID locationId = UUID.randomUUID();
        UUID createdBy = UUID.randomUUID();

        LocationEntity mockEntity = new LocationEntity();
        mockEntity.setId(locationId);
        mockEntity.setCreatedBy(createdBy);
        mockEntity.setPublic(true); // Public location

        when(locationRepository.findById(locationId)).thenReturn(Optional.of(mockEntity));

        // Act
        Optional<LocationResponseDto> result = locationService.deleteLocationByUuid(locationId, createdBy);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void createLocation() {
    }

    @Test
    void getLocationById() {
    }
}
