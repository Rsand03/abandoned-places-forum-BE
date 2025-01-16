package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationPublishDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.exception.ConflictException;
import ee.taltech.iti0302project.app.exception.ForbiddenException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class LocationPublishingService {

    public static final int MIN_DISTANCE_BETWEEN_PUBLIC_LOCATIONS = 50;
    public static final int USER_POINTS_FOR_PUBLISHING_A_LOCATION = 5;

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final LocationMapper locationMapper;


    public Optional<LocationResponseDto> publishLocation(LocationPublishDto locationPublishDto, UUID createdBy) {

        LocationEntity locationToPublish = locationRepository.findById(locationPublishDto.getLocationId())
                .orElseThrow(() -> new ApplicationException("Location not found"));
        if (locationToPublish.isPublic()) {
            throw new ConflictException("Location already public");
        }

        UserEntity userEntity = userRepository.findById(createdBy)
                .orElseThrow(() -> new ApplicationException("User not found"));
        if (userEntity.getId().equals(locationToPublish.getCreatedBy())) {
            throw new ForbiddenException("Location is not created by the user");
        }

        for (LocationEntity publicLocation : locationRepository.findAllByIsPublicTrue()) {
            double distance = calculateDistance(
                    locationToPublish.getLat(),
                    locationToPublish.getLon(),
                    publicLocation.getLat(),
                    publicLocation.getLon()
            );
            if (distance < MIN_DISTANCE_BETWEEN_PUBLIC_LOCATIONS) {
                throw new ConflictException("Location is less than 50 meters away from another location.");
            }
        }

        locationToPublish.setPublic(true);
        locationToPublish.setMinRequiredPointsToView(locationPublishDto.getMinRequiredPointsToView());
        LocationEntity publishedLocation = locationRepository.save(locationToPublish);

        userEntity.setPoints(userEntity.getPoints() + USER_POINTS_FOR_PUBLISHING_A_LOCATION);
        userRepository.save(userEntity);

        return Optional.of(locationMapper.toResponseDto(publishedLocation));
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c * 1000;
    }

}
