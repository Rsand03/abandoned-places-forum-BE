package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.AbandonedPlaceDto;
import ee.taltech.iti0302project.app.service.AbandonedPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/abandoned-places")
@RequiredArgsConstructor
public class AbandonedPlaceController {

    private final AbandonedPlaceService abandonedPlaceService;

    @PostMapping("")
    public ResponseEntity<AbandonedPlaceDto> createAbandonedPlace(@RequestBody AbandonedPlaceDto createdAbandonedPlace) {
        return ResponseEntity.ok(abandonedPlaceService.createAbandonedPlace(createdAbandonedPlace));
    }

    @GetMapping("")
    public ResponseEntity<List<AbandonedPlaceDto>> getAbandonedPlaces(
                @RequestParam(value = "name", required = false) Optional<String> name,
                @RequestParam(value = "type", required = false) Optional<String> type,
                @RequestParam(value = "condition", required = false) Optional<String> condition) {
        return ResponseEntity.ok(abandonedPlaceService.findAbandonedPlacesByAuthorAndGenre(name, type, condition));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbandonedPlaceDto> getAbandonedPlaceById(@PathVariable("id") long id) {
        Optional<AbandonedPlaceDto> abandonedPlace = abandonedPlaceService.findAbandonedPlaceById(id);
        return abandonedPlace.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbandonedPlaceDto> updateAbandonedPlace(
                @PathVariable("id") long id,
                @RequestBody AbandonedPlaceDto updatedAbandonedPlace) {
        Optional<AbandonedPlaceDto> abandonedPlace = abandonedPlaceService.updateAbandonedPlace(id, updatedAbandonedPlace);
        return abandonedPlace.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAbandonedPlace(@PathVariable long id) {
        Optional<AbandonedPlaceDto> abandonedPlace = abandonedPlaceService.removeAbandonedPlace(id);
        return abandonedPlace.isPresent() ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
