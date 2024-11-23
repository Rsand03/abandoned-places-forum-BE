package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationCriteria;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.entity.location.LocationCategoryEntity;
import ee.taltech.iti0302project.app.entity.location.LocationConditionEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.entity.location.LocationStatusEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationCategoryRepository;
import ee.taltech.iti0302project.app.repository.location.LocationConditionRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.repository.location.LocationSpecifications;
import ee.taltech.iti0302project.app.repository.location.LocationStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationCategoryRepository locationCategoryRepository;
    private final LocationConditionRepository locationConditionRepository;
    private final LocationStatusRepository locationStatusRepository;
    private final UserRepository userRepository;

    private final LocationMapper locationMapper;

    public List<LocationResponseDto> getAllLocations() {
        return locationMapper.toDtoList(locationRepository.findAll());
    }

    public Optional<List<LocationResponseDto>> getFilteredLocations(LocationCriteria locationCriteria) {
        return validateLocationCriteria(locationCriteria)
                .map(criteria -> {
            Specification<LocationEntity> spec = Specification.where(null);

            spec = spec.and(LocationSpecifications.hasCreatedBy(criteria.getUserId()));
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

    public Optional<LocationResponseDto> deleteLocationByUuid(UUID uuid, UUID createdBy) {
        return locationRepository.findById(uuid)
                .filter(locationEntity -> locationEntity.getCreatedBy().equals(createdBy))
                .map(locationEntity -> {
                    locationRepository.deleteById(uuid);
                    return locationMapper.toResponseDto(locationEntity);
                });
    }

    public Optional<LocationResponseDto> createLocation(LocationCreateDto createdDto) {
        return validateLocationCreateDto(createdDto)
                .map(dto -> {
                    LocationEntity createdEntity = locationMapper.toEntity(dto);

                    LocationCategoryEntity mainCategory = locationCategoryRepository.findById(dto.getMainCategoryId())
                            .orElseThrow(() -> new EntityNotFoundException("Main category not found"));
                    List<LocationCategoryEntity> subCategories = locationCategoryRepository.findAllById(dto.getSubCategoryIds());
                    LocationConditionEntity condition = locationConditionRepository.findById(dto.getConditionId())
                            .orElseThrow(() -> new EntityNotFoundException("Condition not found"));
                    LocationStatusEntity status = locationStatusRepository.findById(dto.getStatusId())
                            .orElseThrow(() -> new EntityNotFoundException("Status not found"));

                    createdEntity.setMainCategory(mainCategory);
                    createdEntity.setSubCategories(subCategories);
                    createdEntity.setCondition(condition);
                    createdEntity.setStatus(status);

                    return locationMapper.toResponseDto(locationRepository.save(createdEntity));
                });
    }

    private Optional<LocationCreateDto> validateLocationCreateDto(LocationCreateDto createdDto) {
        return Optional.of(createdDto)
                .filter(dto -> dto.getSubCategoryIds().stream()
                        .allMatch(x -> x != null && locationCategoryRepository.existsById(x)))
                .filter(dto -> locationConditionRepository.existsById(dto.getConditionId()))
                .filter(dto -> locationStatusRepository.existsById(dto.getStatusId()))
                .filter(dto -> locationCategoryRepository.existsById(dto.getMainCategoryId()))
                .filter(dto -> userRepository.existsById(dto.getCreatedByUserUuid()));
    }

    private Optional<LocationCriteria> validateLocationCriteria(LocationCriteria validatedCriteria) {
        return Optional.of(validatedCriteria)
                .filter(criteria -> criteria.getSubCategoryIds().stream()
                        .noneMatch(x -> x == null || x < 1 || x > 15))
                .filter(criteria -> userRepository.existsById(criteria.getUserId()));
    }

}
