package ee.taltech.iti0302project.app.controller.location;

import ee.taltech.iti0302project.app.dto.location.attributes.LocationCategoryDto;
import ee.taltech.iti0302project.app.service.location.LocationCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/location-categories")
@RequiredArgsConstructor
public class LocationCategoryController {

    private final LocationCategoryService locationCategoryService;

    @GetMapping("")
    public ResponseEntity<List<LocationCategoryDto>> getLocationCategories() {
        return ResponseEntity.ok(locationCategoryService.getAllLocationCategories());
    }

}
