package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationCriteria;
import ee.taltech.iti0302project.app.dto.location.LocationEditDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.location.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("api/locations")
@RequiredArgsConstructor
@Tag(name = "Locations")
public class LocationController {

    private final LocationService locationService;
    private final JwtService jwtService;

    @Operation(summary = "Retrieve locations matching criteria, defaulting to user-created locations and" +
            " public locations viewable with user's points")
    @ApiResponse(responseCode = "200", description = "Retrieved locations or empty list")
    @ApiResponse(responseCode = "400", description = "Invalid criteria")
    @GetMapping("")
    public ResponseEntity<List<LocationResponseDto>> getFilteredLocations(@Valid @ParameterObject LocationCriteria criteria,
                                                                          @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        criteria.setUserId(userId);
        return locationService.getFilteredLocations(criteria)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Create new private location")
    @ApiResponse(responseCode = "200", description = "Location created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid locationCreateDto")
    @PostMapping("")
    public ResponseEntity<LocationResponseDto> createLocation(@Valid @RequestBody LocationCreateDto locationCreateDto,
                                                              @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        locationCreateDto.setCreatedBy(userId);
        return ResponseEntity.ok(locationService.createLocation(locationCreateDto));
    }

    @Operation(summary = "Edit an existing private location of the user")
    @ApiResponse(responseCode = "200", description = "Location edited successfully")
    @ApiResponse(responseCode = "400", description = "Invalid locationEditDto")
    @ApiResponse(responseCode = "403", description = "Not allowed to modify the location")
    @PatchMapping("")
    public ResponseEntity<LocationResponseDto> editLocation(@Valid @RequestBody LocationEditDto locationEditDto,
                                                            @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        locationEditDto.setEditingUserId(userId);
        return ResponseEntity.ok(locationService.editExistingLocation(locationEditDto));
    }

    @Operation(summary = "Delete an existing private location of the user")
    @ApiResponse(responseCode = "204", description = "Location deleted successfully")
    @ApiResponse(responseCode = "404", description = "No such location")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id,
                                               @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        return locationService.deleteLocationByUuid(id, userId)
                .isPresent() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Retrieve a location based on id")
    @ApiResponse(responseCode = "200", description = "Retrieved location")
    @ApiResponse(responseCode = "404", description = "No such location")
    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDto> getLocationById(@PathVariable UUID id,
                                                               @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        return locationService.getLocationById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
