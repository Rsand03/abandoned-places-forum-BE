package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.LocationMapper;
import ee.taltech.iti0302project.app.entity.location.LocationCategoryEntity;
import ee.taltech.iti0302project.app.entity.location.LocationConditionEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.entity.location.LocationStatusEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationCategoryRepository;
import ee.taltech.iti0302project.app.repository.location.LocationConditionRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.repository.location.LocationStatusRepository;
import lombok.RequiredArgsConstructor;
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

    public Optional<UUID> createLocation(LocationCreateDto createdDto) {
        return validateLocation(createdDto)
                .map(dto -> {
                    LocationEntity createdEntity = locationMapper.toEntity(dto);

                    LocationCategoryEntity mainCategory = locationCategoryRepository.getReferenceById(dto.getMainCategoryId());
                    List<LocationCategoryEntity> subCategories = locationCategoryRepository.findAllById(dto.getSubCategoryIds());
                    LocationConditionEntity condition = locationConditionRepository.getReferenceById(dto.getConditionId());
                    LocationStatusEntity status = locationStatusRepository.getReferenceById(dto.getStatusId());

                    createdEntity.setMainCategory(mainCategory);
                    createdEntity.setSubCategories(subCategories);
                    createdEntity.setCondition(condition);
                    createdEntity.setStatus(status);

                    return locationRepository.save(createdEntity).getId();
                });
    }

    private Optional<LocationCreateDto> validateLocation(LocationCreateDto createdDto) {
        return Optional.of(createdDto)
                .filter(dto -> dto.getSubCategoryIds().stream().allMatch(locationCategoryRepository::existsById))
                .filter(dto -> locationConditionRepository.existsById(dto.getConditionId()))
                .filter(dto -> locationStatusRepository.existsById(dto.getStatusId()))
                .filter(dto -> locationCategoryRepository.existsById(dto.getMainCategoryId()))
                .filter(dto -> userRepository.existsById(dto.getCreatedByUserUuid()));
    }

}
