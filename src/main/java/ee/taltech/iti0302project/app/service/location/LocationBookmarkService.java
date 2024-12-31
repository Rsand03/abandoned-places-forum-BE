package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkCreateDto;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkDto;
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
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class LocationBookmarkService {
    private final LocationBookmarkRepository locationBookmarkRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    private final LocationBookmarkMapper locationBookmarkMapper;

    public List<LocationBookmarkDto> getLocationBookmarksByUserAndLocation(UUID userId, Optional<UUID> locationId) {
        List<LocationBookmarkEntity> bookmarks;

        if (locationId.isPresent()) {
            bookmarks = locationBookmarkRepository.findByCreatedByAndLocationId(userId, locationId.get());
        } else {
            bookmarks = locationBookmarkRepository.findByCreatedBy(userId);
        }

        return bookmarks.stream()
                .map(locationBookmarkMapper::toResponseDto)
                .toList();
    }

    public Optional<LocationBookmarkDto> createLocationBookmark(LocationBookmarkCreateDto locationBookmarkCreateDto) {
        validateLocationBookmark(locationBookmarkCreateDto);

        LocationBookmarkEntity bookmarkEntity = locationBookmarkMapper.toEntity(locationBookmarkCreateDto);

        bookmarkEntity.setLocationId(locationBookmarkCreateDto.getLocationId());
        bookmarkEntity.setType(locationBookmarkCreateDto.getType().getLabel());

        LocationBookmarkEntity savedEntity = locationBookmarkRepository.save(bookmarkEntity);
        return Optional.of(locationBookmarkMapper.toResponseDto(savedEntity));
    }

    private void validateLocationBookmark(LocationBookmarkCreateDto locationBookmarkCreateDto) {
        if (!userRepository.existsById(locationBookmarkCreateDto.getCreatedByUserUuid())) {
            throw new EntityNotFoundException("User not found with ID: "
                    + locationBookmarkCreateDto.getCreatedByUserUuid());
        }

        if (!locationRepository.existsById(locationBookmarkCreateDto.getLocationId())) {
            throw new EntityNotFoundException("Location not found with ID: "
                    + locationBookmarkCreateDto.getLocationId());
        }
    }

    @Transactional
    public void deleteLocationBookmarkByUuid(UUID bookmarkId, UUID userId) {
        boolean exists = locationBookmarkRepository.existsByIdAndCreatedBy(bookmarkId, userId);
        if (!exists) {
            throw new EntityNotFoundException("Bookmark not found for bookmarkId: " + bookmarkId
                    + " and userId: " + userId);
        }

        locationBookmarkRepository.deleteByIdAndCreatedBy(bookmarkId, userId);
    }
}
