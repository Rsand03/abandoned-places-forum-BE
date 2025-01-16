package ee.taltech.iti0302project.test.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationPublishDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.enums.user.UserPointActions;
import ee.taltech.iti0302project.app.exception.ConflictException;
import ee.taltech.iti0302project.app.exception.ForbiddenException;
import ee.taltech.iti0302project.app.exception.NotFoundException;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.service.location.LocationPublishingService;
import ee.taltech.iti0302project.app.service.user.UserPointsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
class LocationPublishingServiceTest {

    private static final double DEFAULT_LON = 25D;
    private static final double DEFAULT_LAT = 59D;

    private static final double LON_45_METERS_AWAY = 25.000475D;
    private static final double LAT_45_METERS_AWAY = 59.000327D;

    private static final double LON_60_METERS_AWAY = 25.000672D;
    private static final double LAT_60_METERS_AWAY = 59.000411D;

    private static final UUID PUBLISHER_UUID = UUID.fromString("68ce8219-45fd-4c01-8ba5-7b84d39d7617");
    private static final long MAX_PUBLIC_LOCATIONS = 1000L;
    private static final int POINTS_FOR_PUBLISHING_LOCATION = 5;

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private UserPointsService userPointsService;
    @Mock
    private LocationMapper locationMapper;


    @InjectMocks
    private LocationPublishingService locationPublishingService;


    private LocationPublishDto defaultLocationPublishDto;
    private LocationEntity publishedLocationEntity;
    private LocationEntity otherLocationEntity;


    @BeforeEach
    void setUp() {
        defaultLocationPublishDto = LocationPublishDto.builder()
                .publisherId(PUBLISHER_UUID)
                .locationId(UUID.fromString("53ce8219-45fd-4c00-8ba5-7b84d29d7617"))
                .minRequiredPointsToView(0)
                .build();

        publishedLocationEntity = new LocationEntity();
        publishedLocationEntity.setCreatedBy(defaultLocationPublishDto.getPublisherId());
        publishedLocationEntity.setId(defaultLocationPublishDto.getLocationId());
        publishedLocationEntity.setPublic(false);
        publishedLocationEntity.setLon(DEFAULT_LON);
        publishedLocationEntity.setLat(DEFAULT_LAT);

        otherLocationEntity = new LocationEntity();
        otherLocationEntity.setLon(LON_60_METERS_AWAY);
        otherLocationEntity.setLat(LAT_60_METERS_AWAY);
    }

    @Test
    void publishLocation_existingPublicLocationIsFarEnough_success() {
        // Given
        given(locationRepository.findById(defaultLocationPublishDto.getLocationId()))
                .willReturn(Optional.of(publishedLocationEntity));
        given(locationRepository.countByIsPublicTrue())
                .willReturn(MAX_PUBLIC_LOCATIONS - 1L);
        given(locationRepository.findAllByIsPublicTrue())
                .willReturn(List.of(otherLocationEntity));  // 60 meters away

        given(locationRepository.save(publishedLocationEntity)).willReturn(publishedLocationEntity);
        given(userPointsService.giveUserPoints(UserPointActions.PUBLISH_LOCATION, PUBLISHER_UUID))
                .willReturn(POINTS_FOR_PUBLISHING_LOCATION);

        given(locationMapper.toResponseDto(publishedLocationEntity)).willReturn(new LocationResponseDto());

        // When
        locationPublishingService.publishLocation(defaultLocationPublishDto);

        // Then
        then(locationRepository).shouldHaveNoMoreInteractions();
        then(userPointsService).shouldHaveNoMoreInteractions();
    }

    @Test
    void publishLocation_existingPublicLocationIsTooClose_error() {
        // Given
        given(locationRepository.findById(publishedLocationEntity.getId()))
                .willReturn(Optional.of(publishedLocationEntity));
        given(locationRepository.countByIsPublicTrue())
                .willReturn(MAX_PUBLIC_LOCATIONS - 1L);

        otherLocationEntity.setLon(LON_45_METERS_AWAY);
        otherLocationEntity.setLat(LAT_45_METERS_AWAY);

        given(locationRepository.findAllByIsPublicTrue())
                .willReturn(List.of(otherLocationEntity));  // 45 meters away

        // When
        Throwable thrown = catchThrowable(() -> locationPublishingService.publishLocation(defaultLocationPublishDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ConflictException.class);

        then(locationRepository).shouldHaveNoMoreInteractions();
        then(userPointsService).shouldHaveNoMoreInteractions();
    }

    @Test
    void publishLocation_locationDoesNotExist_error() {
        // Given
        given(locationRepository.findById(publishedLocationEntity.getId()))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> locationPublishingService.publishLocation(defaultLocationPublishDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Location not found");

        then(locationRepository).shouldHaveNoMoreInteractions();
        then(userPointsService).shouldHaveNoMoreInteractions();
    }

    @Test
    void publishLocation_locationIsAlreadyPublic_error() {
        // Given
        publishedLocationEntity.setPublic(true);

        given(locationRepository.findById(publishedLocationEntity.getId()))
                .willReturn(Optional.of(publishedLocationEntity));

        // When
        Throwable thrown = catchThrowable(() -> locationPublishingService.publishLocation(defaultLocationPublishDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ConflictException.class)
                .hasMessage("Location already public");

        then(locationRepository).shouldHaveNoMoreInteractions();
        then(userPointsService).shouldHaveNoMoreInteractions();
    }

    @Test
    void publishLocation_tooManyPublicLocations_error() {
        // Given
        given(locationRepository.findById(publishedLocationEntity.getId()))
                .willReturn(Optional.of(publishedLocationEntity));
        given(locationRepository.countByIsPublicTrue())
                .willReturn(MAX_PUBLIC_LOCATIONS);

        // When
        Throwable thrown = catchThrowable(() -> locationPublishingService.publishLocation(defaultLocationPublishDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ConflictException.class)
                .hasMessage("Too many public locations");

        then(locationRepository).shouldHaveNoMoreInteractions();
        then(userPointsService).shouldHaveNoMoreInteractions();
    }

    @Test
    void publishLocation_locationIsNotCreatedByTheUser_error() {
        // Given
        defaultLocationPublishDto.setPublisherId(UUID.fromString("99ce8219-45fd-4c11-8ba5-7b84d29d7617"));

        given(locationRepository.findById(publishedLocationEntity.getId()))
                .willReturn(Optional.of(publishedLocationEntity));
        given(locationRepository.countByIsPublicTrue())
                .willReturn(MAX_PUBLIC_LOCATIONS - 1L);

        // When
        Throwable thrown = catchThrowable(() -> locationPublishingService.publishLocation(defaultLocationPublishDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Location is not created by the user");

        then(locationRepository).shouldHaveNoMoreInteractions();
        then(userPointsService).shouldHaveNoMoreInteractions();
    }

}
