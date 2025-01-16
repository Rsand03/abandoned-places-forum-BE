package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.bookmark.BookmarkType;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkCreateDto;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkDto;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.location.LocationBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "LocationBookmark", description = "LocationBookmark management APIs")
public class LocationBookmarkController {

    private final LocationBookmarkService locationBookmarkService;
    private final JwtService jwtService;

    @Operation(description = "Retrieve location bookmark by userId and locationId")
    @ApiResponse(responseCode = "200", description = "LocationBookmarks retrieved successfully")
    @GetMapping("")
    public ResponseEntity<List<LocationBookmarkDto>> getLocationBookmarks(
            @RequestParam(value = "locationId", required = false) Optional<UUID> locationId,
            @RequestHeader("Authorization") String authHeader
    ) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        List<LocationBookmarkDto> bookmarks = locationBookmarkService.getLocationBookmarksByUserAndLocation(userId, locationId);
        return ResponseEntity.ok(bookmarks);
    }

    @Operation(
            summary = "Create a new locationBookmark",
            description = "Allows users to create new locationBookmarks for locations"
    )
    @ApiResponse(responseCode = "200", description = "LocationBookmark created successfully")
    @PostMapping("")
    public ResponseEntity<LocationBookmarkDto> createLocationBookmark(
            @Valid @RequestBody LocationBookmarkCreateDto createdLocationBookmark) {
        return locationBookmarkService.createLocationBookmark(createdLocationBookmark)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Delete a locationBookmark",
            description = "Allows users to delete locationBookmarks of specific locations"
    )
    @ApiResponse(responseCode = "204", description = "Bookmark deleted successfully")
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
