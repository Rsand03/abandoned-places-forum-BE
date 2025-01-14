package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.attributes.LocationAttributesDto;
import ee.taltech.iti0302project.app.service.location.LocationAttributesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/locations/attributes")
@RequiredArgsConstructor
@Tag(name = "Location attributes")
public class LocationAttributesController {

    private final LocationAttributesService locationAttributesService;

    @Operation(summary = "Retrieve all location categories, conditions and statuses")
    @ApiResponse(responseCode = "200", description = "Retrieved all location attributes")
    @GetMapping("")
    public ResponseEntity<LocationAttributesDto> getLocationAttributes() {
        return ResponseEntity.ok(locationAttributesService.getLocationAttributes());
    }

}
