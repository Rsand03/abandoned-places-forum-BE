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
import ee.taltech.iti0302project.app.repository.location.*;
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
    private final LocationBookmarkRepository locationBookmarkRepository;

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

                    if (criteria.getBookmarkTypes() != null) {
                        spec = spec.and(LocationSpecifications.hasBookmarkTypes(criteria.getBookmarkTypes()));
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
                    locationBookmarkRepository.deleteAllByLocationIdAndCreatedBy(locationId, createdBy);
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


}
