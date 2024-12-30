package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationCriteria;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationAttributesDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationCategoryMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationConditionMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationStatusMapper;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationCategoryRepository;
import ee.taltech.iti0302project.app.repository.location.LocationConditionRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.repository.location.LocationSpecifications;
import ee.taltech.iti0302project.app.repository.location.LocationStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationCategoryRepository locationCategoryRepository;
    private final LocationConditionRepository locationConditionRepository;
    private final LocationStatusRepository locationStatusRepository;
    private final UserRepository userRepository;

    private final LocationMapper locationMapper;
    private final LocationCategoryMapper categoryMapper;
    private final LocationConditionMapper conditionMapper;
    private final LocationStatusMapper statusMapper;


    public LocationAttributesDto getLocationAttributes() {
        LocationAttributesDto attributesDto = new LocationAttributesDto();
        attributesDto.setCategories(categoryMapper.toDtoList(locationCategoryRepository.findAll()));
        attributesDto.setConditions(conditionMapper.toDtoList(locationConditionRepository.findAll()));
        attributesDto.setStatuses(statusMapper.toDtoList(locationStatusRepository.findAll()));
        return attributesDto;
    }

    public Optional<List<LocationResponseDto>> getFilteredLocations(LocationCriteria locationCriteria) {
        return validateLocationCriteria(locationCriteria)
                .map(criteria -> {
                    Specification<LocationEntity> spec = Specification.where(null);

                    spec = spec.and(LocationSpecifications.isPublicOrHasCreatedBy(criteria.getUserId()));
                    if (criteria.getMainCategoryId() != null) {
                        spec = spec.and(LocationSpecifications.hasMainCategory(criteria.getMainCategoryId()));
                    }
                    if (!criteria.getSubCategoryIds().isEmpty()) {
                        spec = spec.and(LocationSpecifications.hasSubcategories(criteria.getSubCategoryIds()));
                    }
                    if (criteria.getConditionId() != null) {
                        spec = spec.and(LocationSpecifications.hasCondition(criteria.getConditionId()));
                    }
                    if (criteria.getStatusId() != null) {
                        spec = spec.and(LocationSpecifications.hasStatus(criteria.getStatusId()));
                    }
                    if (criteria.getMinRequiredPointsToView() != null) {
                        spec = spec.and(LocationSpecifications.minPointsToViewHigherThan(criteria.getMinRequiredPointsToView()));
                    }
                    return locationMapper.toDtoList(locationRepository.findAll(spec));
                });
    }

    private Optional<LocationCriteria> validateLocationCriteria(LocationCriteria validatedCriteria) {
        return Optional.of(validatedCriteria)
                .filter(criteria -> criteria.getSubCategoryIds().stream()
                        .noneMatch(x -> x == null || x < 1 || x > 15))
                .filter(criteria -> userRepository.existsById(criteria.getUserId()));
    }

    public Optional<LocationResponseDto> deleteLocationByUuid(UUID locationId, UUID createdBy) {
        return locationRepository.findById(locationId)
                .filter(locationEntity -> locationEntity.getCreatedBy().equals(createdBy))
                .filter(locationEntity -> !locationEntity.isPublic())
                .map(locationEntity -> {
                    locationRepository.deleteById(locationId);
                    log.info("deleted location with id " + locationEntity.getId());
                    return locationMapper.toResponseDto(locationEntity);
                });
    }

    public LocationResponseDto createLocation(LocationCreateDto locationCreateDto) {
        return validateLocationCreateDto(locationCreateDto)
                .map(dto -> {
                    LocationEntity newLocationEntity = locationMapper.toEntity(dto);

                    newLocationEntity.setCreatedBy(dto.getCreatedBy());
                    newLocationEntity.setSubCategories(locationCategoryRepository.findAllById(dto.getSubCategoryIds()));

                    newLocationEntity.setMainCategory(locationCategoryRepository.findById(dto.getMainCategoryId())
                            .orElseThrow(() -> new ApplicationException("Invalid main category id")));
                    newLocationEntity.setCondition(locationConditionRepository.findById(dto.getConditionId())
                            .orElseThrow(() -> new ApplicationException("Invalid condition id")));
                    newLocationEntity.setStatus(locationStatusRepository.findById(dto.getStatusId())
                            .orElseThrow(() -> new ApplicationException("Invalid status id")));

                    LocationEntity createdEntity = locationRepository.save(newLocationEntity);
                    log.info("Created location with id " + createdEntity.getId());

                    return locationMapper.toResponseDto(createdEntity);
                }).orElseThrow(() -> new ApplicationException("Invalid user or subcategories"));
    }

    private Optional<LocationCreateDto> validateLocationCreateDto(LocationCreateDto createdDto) {
        return Optional.of(createdDto)
                .filter(dto -> dto.getCreatedBy() != null && userRepository.existsById(dto.getCreatedBy()))
                .filter(dto -> dto.getSubCategoryIds().stream()
                        .allMatch(x -> x != null
                                && locationCategoryRepository.existsById(x)
                                && !x.equals(dto.getMainCategoryId())));
    }

    public Optional<LocationResponseDto> getLocationById(UUID locationId, UUID userId) {
        return locationRepository.findById(locationId)
                .filter(x -> x.isPublic() || x.getCreatedBy().equals(userId))
                .map(locationMapper::toResponseDto);
    }


    public Optional<LocationResponseDto> publishLocation(UUID uuid, UUID createdBy, int minRequiredPoints) {
        LocationEntity location = locationRepository.findById(uuid)
                .filter(locationEntity -> locationEntity.getCreatedBy().equals(createdBy))
                .orElseThrow(() -> new ApplicationException("Location not found"));
        // TODO: add loc details to post
        List<LocationEntity> allLocations = locationRepository.findAll();
        for (LocationEntity otherLocation : allLocations) {
            if (!otherLocation.getId().equals(uuid)) {
                double distance = calculateDistance(
                        location.getLat(),
                        location.getLon(),
                        otherLocation.getLat(),
                        otherLocation.getLon()
                );
                if (distance < 50) {
                    throw new ApplicationException("Location is less than 50 meters away from another location.");
                }
            }
        }

        location.setPublic(true);
        location.setMinRequiredPointsToView(minRequiredPoints);

        locationRepository.save(location);

        return Optional.of(locationMapper.toResponseDto(location));
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c * 1000;
    }

}
