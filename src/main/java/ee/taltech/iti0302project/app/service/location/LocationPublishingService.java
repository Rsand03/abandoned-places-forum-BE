package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationPublishDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.enums.user.UserPointActions;
import ee.taltech.iti0302project.app.exception.ConflictException;
import ee.taltech.iti0302project.app.exception.ForbiddenException;
import ee.taltech.iti0302project.app.exception.NotFoundException;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.service.user.UserPointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class LocationPublishingService {

    public static final Integer MIN_DISTANCE_BETWEEN_PUBLIC_LOCATIONS = 50;
    public static final int MAX_PUBLIC_LOCATIONS = 1000;

    private final LocationRepository locationRepository;
    private final UserPointsService userPointsService;
    private final LocationMapper locationMapper;


    public LocationResponseDto publishLocation(LocationPublishDto publishDto) {

        LocationEntity locationToPublish = locationRepository.findById(publishDto.getLocationId())
                .orElseThrow(() -> new NotFoundException("Location not found"));

        if (locationToPublish.isPublic()) {
            throw new ConflictException("Location already public");
        } else if (locationRepository.countByIsPublicTrue() >= MAX_PUBLIC_LOCATIONS) {
            throw new ConflictException("Too many public locations");
        } else if (!publishDto.getPublisherId().equals(locationToPublish.getCreatedBy())) {
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
                throw new ConflictException(
                        String.format("Location is less than %d meters away from another public location.",
                        MIN_DISTANCE_BETWEEN_PUBLIC_LOCATIONS)
                );
            }
        }

        locationToPublish.setPublic(true);
        locationToPublish.setMinRequiredPointsToView(publishDto.getMinRequiredPointsToView());
        LocationEntity publishedLocation = locationRepository.save(locationToPublish);

        Integer earnedPoints = userPointsService.giveUserPoints(
                UserPointActions.PUBLISH_LOCATION, publishDto.getPublisherId()
        );

        log.info("User {} published location named {} and earned {} points",
                publishDto.getPublisherId(), publishedLocation.getName(), earnedPoints);

        return locationMapper.toResponseDto(publishedLocation);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c * MAX_PUBLIC_LOCATIONS;
    }

}
