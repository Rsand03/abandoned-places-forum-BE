package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.attributes.LocationAttributesDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationConditionDto;
import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationCriteria;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationStatusDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import ee.taltech.iti0302project.app.service.location.LocationConditionService;
import ee.taltech.iti0302project.app.service.location.LocationService;
import ee.taltech.iti0302project.app.service.location.LocationStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationConditionService locationConditionService;
    private final LocationStatusService locationStatusService;
    private final LocationService locationService;
    private final AuthService authService;

    @GetMapping("conditions")
    public ResponseEntity<List<LocationConditionDto>> getLocationConditions() {
        return ResponseEntity.ok(locationConditionService.getAllLocationConditions());
    }

    @GetMapping("statuses")
    public ResponseEntity<List<LocationStatusDto>> getLocationStatuses() {
        return ResponseEntity.ok(locationStatusService.getAllLocationStatuses());
    }

    @GetMapping("attributes")
    public ResponseEntity<LocationAttributesDto> getLocationAttributes() {
        return ResponseEntity.ok(locationService.getLocationAttributes());
    }

    @GetMapping("")
    public ResponseEntity<List<LocationResponseDto>> getFilteredLocations(
            @Valid @ModelAttribute LocationCriteria criteria,
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = authService.extractUserIdFromToken(authHeader);
        criteria.setUserId(userId);
        return locationService.getFilteredLocations(criteria)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("")
    public ResponseEntity<LocationResponseDto> createLocation(@Valid @RequestBody LocationCreateDto locationCreateDto,
                                                              @RequestHeader("Authorization") String authHeader) {
        UUID userId = authService.extractUserIdFromToken(authHeader);
        locationCreateDto.setCreatedBy(userId);
        return ResponseEntity.ok(locationService.createLocation(locationCreateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id,
                                               @RequestHeader("Authorization") String authHeader) {
        UUID userId = authService.extractUserIdFromToken(authHeader);
        return locationService.deleteLocationByUuid(id, userId)
                .isPresent() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDto> getLocationById(@PathVariable UUID id,
                                                               @RequestHeader("Authorization") String authHeader) {
        UUID userId = authService.extractUserIdFromToken(authHeader);
        return locationService.getLocationById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/publishLocation")
    public ResponseEntity<LocationResponseDto> publishLocation(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam UUID locationId,
            @RequestParam int minRequiredPoints
    ) {
        UUID userId = authService.extractUserIdFromToken(authHeader);

        // Call service to publish location
        return locationService.publishLocation(locationId, userId, minRequiredPoints)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
