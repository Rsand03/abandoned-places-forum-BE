package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.LocationTypeDto;
import ee.taltech.iti0302project.app.service.LocationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/location-types")
@RequiredArgsConstructor
public class LocationTypeController {

    private final LocationTypeService locationTypeService;

    @GetMapping("")
    public ResponseEntity<List<LocationTypeDto>> getAbandonedPlaces() {
        return ResponseEntity.ok(locationTypeService.getAllLocationTypes());
    }

}
