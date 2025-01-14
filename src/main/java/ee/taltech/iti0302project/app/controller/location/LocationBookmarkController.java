package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.bookmark.BookmarkType;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkCreateDto;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import ee.taltech.iti0302project.app.service.auth.JwtService;
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
    private final JwtService jwtService;

    @GetMapping("")
    public ResponseEntity<List<LocationBookmarkDto>> getLocationBookmarks(
            @RequestParam(value = "locationId", required = false) Optional<UUID> locationId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
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
            @RequestParam BookmarkType bookmarkType,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        locationBookmarkService.deleteLocationBookmark(userId, locationId, bookmarkType);
        return ResponseEntity.noContent().build();
    }
}
