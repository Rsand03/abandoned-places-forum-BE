package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.*;
import ee.taltech.iti0302project.app.service.location.LocationBookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/location-bookmarks")
@RequiredArgsConstructor
@Validated
public class LocationBookmarkController {

    private final LocationBookmarkService locationBookmarkService;

    @GetMapping("")
    public ResponseEntity<List<LocationBookmarkDto>> getLocationBookmarks(
            @RequestParam(value = "userId", required = true) UUID userId,
            @RequestParam(value = "locationId", required = false) Optional<UUID> locationId
    ) {
        List<LocationBookmarkDto> bookmarks = locationBookmarkService.getLocationBookmarksByUserAndLocation(userId, locationId);
        return ResponseEntity.ok(bookmarks);
    }

    @PostMapping("")
    public ResponseEntity<LocationBookmarkDto> createLocationBookmark(
            @Valid @RequestBody LocationBookmarkCreateDto createdLocationBookmark) {
        return locationBookmarkService.createLocationBookmark(createdLocationBookmark)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteLocationBookmark(
            @RequestParam UUID locationId,
            @RequestParam UUID userId) {
        locationBookmarkService.deleteLocationBookmarkByUuid(locationId, userId);
        return ResponseEntity.noContent().build();
    }
}
