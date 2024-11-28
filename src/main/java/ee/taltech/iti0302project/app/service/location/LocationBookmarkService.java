package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationBookmarkCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationBookmarkDto;
import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationBookmarkMapper;
import ee.taltech.iti0302project.app.entity.location.*;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationBookmarkRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class LocationBookmarkService {
    private final LocationBookmarkRepository locationBookmarkRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    private final LocationBookmarkMapper locationBookmarkMapper;

    public Optional<LocationBookmarkDto> createLocationBookmark(LocationBookmarkCreateDto locationBookmarkCreateDto) {
        return validateLocationBookmark(locationBookmarkCreateDto)
                .map(dto -> {
                    LocationBookmarkEntity createdEntity = locationBookmarkMapper.toEntity(dto);

                    LocationEntity location = locationRepository.findById(dto.getLocationId())
                            .orElseThrow(() -> new EntityNotFoundException("Location not found"));
                    createdEntity.setLocation(location);

                    return locationBookmarkMapper.toDto(locationRepository.save(createdEntity));
                });
    }

    private Optional<LocationBookmarkCreateDto> validateLocationBookmark(LocationBookmarkCreateDto locationBookmarkCreateDto) {
        return Optional.of(locationBookmarkCreateDto)
                .filter(dto -> locationRepository.existsById(dto.getLocationId()))
                .filter(dto -> userRepository.existsById(dto.getCreatedByUserUuid()));
    }
}
