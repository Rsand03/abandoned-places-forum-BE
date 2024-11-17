package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
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
import ee.taltech.iti0302project.app.repository.location.LocationStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    public Optional<LocationResponseDto> createLocation(LocationCreateDto createdDto) {
        return validateLocation(createdDto)
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

    private Optional<LocationCreateDto> validateLocation(LocationCreateDto createdDto) {
        return Optional.of(createdDto)
                .filter(dto -> dto.getSubCategoryIds().stream()
                        .allMatch(x -> x != null && locationCategoryRepository.existsById(x)))
                .filter(dto -> locationConditionRepository.existsById(dto.getConditionId()))
                .filter(dto -> locationStatusRepository.existsById(dto.getStatusId()))
                .filter(dto -> locationCategoryRepository.existsById(dto.getMainCategoryId()))
                .filter(dto -> userRepository.existsById(dto.getCreatedByUserUuid()));
    }

}
