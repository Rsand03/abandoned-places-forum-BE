package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.LocationConditionDto;
import ee.taltech.iti0302project.app.dto.LocationStatusDto;
import ee.taltech.iti0302project.app.service.LocationConditionService;
import ee.taltech.iti0302project.app.service.LocationStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationConditionService locationConditionService;
    private final LocationStatusService locationStatusService;


    @GetMapping("conditions")
    public ResponseEntity<List<LocationConditionDto>> getLocationConditions() {
        return ResponseEntity.ok(locationConditionService.getAllLocationConditions());
    }

    @GetMapping("statuses")
    public ResponseEntity<List<LocationStatusDto>> getLocationStatuses() {
        return ResponseEntity.ok(locationStatusService.getAllLocationStatuses());
    }

}
