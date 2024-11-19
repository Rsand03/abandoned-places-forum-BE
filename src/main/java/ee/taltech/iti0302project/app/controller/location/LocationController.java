package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.LocationConditionDto;
import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.location.LocationStatusDto;
import ee.taltech.iti0302project.app.service.location.LocationConditionService;
import ee.taltech.iti0302project.app.service.location.LocationService;
import ee.taltech.iti0302project.app.service.location.LocationStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/locations")
@RequiredArgsConstructor
@Validated
public class LocationController {

    private final LocationConditionService locationConditionService;
    private final LocationStatusService locationStatusService;
    private final LocationService locationService;

    @GetMapping("conditions")
    public ResponseEntity<List<LocationConditionDto>> getLocationConditions() {
        return ResponseEntity.ok(locationConditionService.getAllLocationConditions());
    }

    @GetMapping("statuses")
    public ResponseEntity<List<LocationStatusDto>> getLocationStatuses() {
        return ResponseEntity.ok(locationStatusService.getAllLocationStatuses());
    }

    @GetMapping("")
    public ResponseEntity<List<LocationResponseDto>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @PostMapping("")
    public ResponseEntity<LocationResponseDto> createLocation(@Valid @RequestBody LocationCreateDto createdLocation) {
        return locationService.createLocation(createdLocation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteLocation(@RequestParam UUID locationId,
                                               @RequestParam UUID userId) {
        return locationService.deleteLocationByUuid(locationId, userId)
                .isPresent() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
