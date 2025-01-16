package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationCriteria;
import ee.taltech.iti0302project.app.dto.location.LocationEditDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.exception.ConflictException;
import ee.taltech.iti0302project.app.exception.NotFoundException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationBookmarkRepository;
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

    public static final long PRIVATE_LOCATIONS_PER_USER = 250;  // add to application.properties (in server too)

    private final LocationRepository locationRepository;
    private final LocationCategoryRepository locationCategoryRepository;
    private final LocationConditionRepository locationConditionRepository;
    private final LocationStatusRepository locationStatusRepository;
    private final UserRepository userRepository;
    private final LocationBookmarkRepository locationBookmarkRepository;

    private final LocationMapper locationMapper;


    @Transactional(readOnly = true)
    public Optional<LocationResponseDto> getLocationById(UUID locationId, UUID userId) {
        return locationRepository.findById(locationId)
                .filter(x -> x.isPublic() || x.getCreatedBy().equals(userId))
                .map(locationMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Optional<List<LocationResponseDto>> getFilteredLocations(LocationCriteria locationCriteria) {
        return validateLocationCriteria(locationCriteria)
                .map(criteria -> {
                    locationCriteria.setUserPoints(userRepository.findUserPointsById(locationCriteria.getUserId()));

                    Specification<LocationEntity> spec = Specification.where(null);

                    spec = spec.and(LocationSpecifications.hasCreatedByOrIsPublicAndUserHasEnoughPoints(
                            criteria.getUserId(),
                            criteria.getUserPoints())
                    );

                    if (criteria.isFilterByMainCategoryOnly()) {
                        spec = spec.and(LocationSpecifications.hasMainCategory(criteria.getCategoryIds()));
                    } else {
                        spec = spec.and(LocationSpecifications.hasMainCategoryOrSubcategory(criteria.getCategoryIds()));
                    }

                    if (criteria.getConditionIds() != null) {
                        spec = spec.and(LocationSpecifications.hasCondition(criteria.getConditionIds()));
                    }
                    if (criteria.getStatusIds() != null) {
                        spec = spec.and(LocationSpecifications.hasStatus(criteria.getStatusIds()));
                    }
                    if (criteria.getBookmarkTypes() != null) {
                        spec = spec.and(LocationSpecifications.hasBookmarkTypes(criteria.getBookmarkTypes()));
                    }

                    return locationMapper.toDtoList(locationRepository.findAll(spec));
                });
    }

    private Optional<LocationCriteria> validateLocationCriteria(LocationCriteria validatedCriteria) {
        return Optional.of(validatedCriteria)
                .filter(criteria -> criteria.getCategoryIds().stream()
                        .noneMatch(x -> x == null || x < 1 || x > 15))
                .filter(criteria -> criteria.getConditionIds().stream()
                        .noneMatch(x -> x == null || x < 1 || x > 6))
                .filter(criteria -> criteria.getStatusIds().stream()
                        .noneMatch(x -> x == null || x < 1 || x > 8))
                .filter(criteria -> userRepository.existsById(criteria.getUserId()));
    }

    // Variant 1: no optional streams; specific error messages; easier to unit-test
    public LocationResponseDto createLocation(LocationCreateDto dto) {

        if (!userRepository.existsById(dto.getCreatedBy())) {
            throw new NotFoundException("No such user");
        } else if (locationRepository.countByIsPublicFalseAndCreatedBy(dto.getCreatedBy()) >= PRIVATE_LOCATIONS_PER_USER) {
            throw new ConflictException("User exceeded maximum amount of private locations");
        }  else if (dto.getSubCategoryIds().contains(dto.getMainCategoryId())) {
            throw new ApplicationException("Duplicate of main category in subcategories");
        } else if (dto.getSubCategoryIds().stream().anyMatch(x -> x == null || !locationCategoryRepository.existsById(x))) {
            throw new ApplicationException("Invalid subcategories");
        }

        LocationEntity newLocationEntity = locationMapper.toEntity(dto);

        newLocationEntity.setCreatedBy(dto.getCreatedBy());
        newLocationEntity.setSubCategories(locationCategoryRepository.findAllById(dto.getSubCategoryIds()));
        newLocationEntity.setMainCategory(locationCategoryRepository.findById(dto.getMainCategoryId())
                .orElseThrow(() -> new NotFoundException("No such main category id")));
        newLocationEntity.setCondition(locationConditionRepository.findById(dto.getConditionId())
                .orElseThrow(() -> new NotFoundException("No such condition id")));
        newLocationEntity.setStatus(locationStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new NotFoundException("No such status id")));

        LocationEntity createdEntity = locationRepository.save(newLocationEntity);

        log.info("Created location with id " + createdEntity.getId());
        return locationMapper.toResponseDto(createdEntity);
    }

    // Variant 2: optional streams; no specific error messages; hard to unit-test
    public LocationResponseDto editExistingLocation(LocationEditDto locationCreateDto) {
        return validateLocationEditDto(locationCreateDto)
                .map(dto -> {
                    LocationEntity prevLocationEntity = locationRepository.findById(dto.getId())
                            .orElseThrow(() -> new NotFoundException("No such location"));

                    prevLocationEntity.setName(dto.getName());
                    prevLocationEntity.setSubCategories(locationCategoryRepository.findAllById(dto.getSubCategoryIds()));
                    prevLocationEntity.setMainCategory(locationCategoryRepository.findById(dto.getMainCategoryId())
                            .orElseThrow(() -> new NotFoundException("No such main category id")));
                    prevLocationEntity.setCondition(locationConditionRepository.findById(dto.getConditionId())
                            .orElseThrow(() -> new NotFoundException("No such condition id")));
                    prevLocationEntity.setStatus(locationStatusRepository.findById(dto.getStatusId())
                            .orElseThrow(() -> new NotFoundException("No such status id")));
                    prevLocationEntity.setAdditionalInformation(dto.getAdditionalInformation());

                    LocationEntity editedEntity = locationRepository.save(prevLocationEntity);

                    log.info("Edited location with id " + editedEntity.getId());

                    return locationMapper.toResponseDto(editedEntity);
                }).orElseThrow(() -> new ApplicationException("Invalid user, subcategories or unauthorized to edit"));
    }

    private Optional<LocationEditDto> validateLocationEditDto(LocationEditDto locationEditDto) {
        return Optional.of(locationEditDto)
                .filter(dto -> userRepository.existsById(dto.getEditingUserId()))
                .filter(dto -> locationRepository.findById(dto.getId())
                        .filter(location -> location.getCreatedBy().equals(dto.getEditingUserId()))
                        .map(location -> !location.isPublic())
                        .orElse(false))
                .filter(dto -> dto.getSubCategoryIds().stream()
                        .allMatch(x -> x != null
                                && locationCategoryRepository.existsById(x)
                                && !x.equals(dto.getMainCategoryId())));
    }

    public Optional<LocationResponseDto> deleteLocationByUuid(UUID locationId, UUID userId) {
        return locationRepository.findById(locationId)
                .filter(locationEntity -> locationEntity.getCreatedBy().equals(userId))
                .filter(locationEntity -> !locationEntity.isPublic())
                .map(locationEntity -> {
                    locationBookmarkRepository.deleteAllByLocationAndCreatedBy(locationEntity, userId);
                    locationRepository.deleteById(locationId);
                    log.info("deleted location with id " + locationEntity.getId());
                    return locationMapper.toResponseDto(locationEntity);
                });
    }

}
