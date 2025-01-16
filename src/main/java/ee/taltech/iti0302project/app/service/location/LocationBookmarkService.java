package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.bookmark.BookmarkType;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkCreateDto;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationBookmarkMapper;
import ee.taltech.iti0302project.app.entity.location.*;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.LocationBookmarkRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
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
            bookmarks = locationBookmarkRepository.findByCreatedByAndLocation_Id(userId, locationId.get());
        } else {
            bookmarks = locationBookmarkRepository.findByCreatedBy(userId);
        }

        return bookmarks.stream()
                .map(locationBookmarkMapper::toResponseDto)
                .toList();
    }

    public Optional<LocationBookmarkDto> createLocationBookmark(LocationBookmarkCreateDto locationBookmarkCreateDto) {
        validateLocationBookmark(locationBookmarkCreateDto);

        LocationEntity locationEntity = locationRepository.findById(locationBookmarkCreateDto.getLocationId())
                .orElseThrow(() -> new ApplicationException("Location not found"));

        LocationBookmarkEntity bookmarkEntity = locationBookmarkMapper.toEntity(locationBookmarkCreateDto);

        bookmarkEntity.setLocation(locationEntity);
        bookmarkEntity.setType(locationBookmarkCreateDto.getType().getLabel());

        LocationBookmarkEntity savedEntity = locationBookmarkRepository.save(bookmarkEntity);
        return Optional.of(locationBookmarkMapper.toResponseDto(savedEntity));
    }

    private void validateLocationBookmark(LocationBookmarkCreateDto locationBookmarkCreateDto) {
        if (!userRepository.existsById(locationBookmarkCreateDto.getCreatedByUserUuid())) {
            throw new ApplicationException("User not found with ID: "
                    + locationBookmarkCreateDto.getCreatedByUserUuid());
        }

        if (!locationRepository.existsById(locationBookmarkCreateDto.getLocationId())) {
            throw new ApplicationException("Location not found with ID: "
                    + locationBookmarkCreateDto.getLocationId());
        }
    }

    @Transactional
    public void deleteLocationBookmark(UUID userId, UUID locationId, BookmarkType bookmarkType) {
        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new ApplicationException("Location not found"));

        boolean exists = locationBookmarkRepository.existsByCreatedByAndLocationAndType(userId, locationEntity,
                bookmarkType.getLabel());

        if (!exists) {
            throw new ApplicationException("Bookmark not found for locationId: " + locationId
                    + " and userId: " + userId + " and type: " + bookmarkType);
        }

        locationBookmarkRepository.deleteByCreatedByAndLocationAndType(userId, locationEntity,
                bookmarkType.getLabel());
    }
}
