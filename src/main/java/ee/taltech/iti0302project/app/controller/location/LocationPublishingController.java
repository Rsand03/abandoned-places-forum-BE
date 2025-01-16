package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.LocationPublishDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.location.LocationPublishingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("api/locations")
@RequiredArgsConstructor
@Tag(name = "Locations publishing")
public class LocationPublishingController {

    private final LocationPublishingService locationPublishingService;
    private final JwtService jwtService;

    @Operation(summary = "Publish a private location and set a minimum points requirement for other users to see it")
    @ApiResponse(responseCode = "200", description = "Location published successfully")
    @ApiResponse(responseCode = "400", description = "Invalid locationPublishDto")
    @ApiResponse(responseCode = "403", description = "Not allowed to modify the location")
    @PatchMapping("/publish")
    public ResponseEntity<LocationResponseDto> publishLocation(@Valid @RequestBody LocationPublishDto locationPublishDto,
                                                               @RequestHeader("Authorization") String authHeader) {
        UUID userId = jwtService.extractUserIdFromAuthHeader(authHeader);
        locationPublishDto.setPublisherId(userId);

        return ResponseEntity.ok(locationPublishingService.publishLocation(locationPublishDto));
    }

}
