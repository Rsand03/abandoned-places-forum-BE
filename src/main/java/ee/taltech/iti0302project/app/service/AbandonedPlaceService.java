package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.AbandonedPlaceDto;
import ee.taltech.iti0302project.app.dto.mapper.AbandonedPlaceMapper;
import ee.taltech.iti0302project.app.entity.AbandonedPlaceEntity;
import ee.taltech.iti0302project.app.repository.AbandonedPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AbandonedPlaceService {

    private final AbandonedPlaceRepository abandonedPlaceRepository;
    private final AbandonedPlaceMapper abandonedPlaceMapper;

    public AbandonedPlaceDto createAbandonedPlace(AbandonedPlaceDto createdAbandonedPlace) {
        AbandonedPlaceEntity entity = abandonedPlaceMapper.toEntity(createdAbandonedPlace);
        abandonedPlaceRepository.save(entity);
        return abandonedPlaceMapper.toDto(entity);
    }

    public List<AbandonedPlaceDto> findAbandonedPlacesByAuthorAndGenre(Optional<String> name,
                                                                       Optional<String> type,
                                                                       Optional<String> condition) {
        return abandonedPlaceMapper.toDtoList(abandonedPlaceRepository.findAll()).stream()
                .filter(x -> name
                        .map(s -> x.getName().equals(s))
                        .orElse(true))
                .filter(x -> type
                        .map(s -> x.getType().equals(s))
                        .orElse(true))
                .filter(x -> condition
                        .map(string -> x.getCondition().equals(string))
                        .orElse(true))
                .toList();
    }

    public Optional<AbandonedPlaceDto> findAbandonedPlaceById(long id) {
        return abandonedPlaceMapper.toDtoList(abandonedPlaceRepository.findAll()).stream()
                .filter(x -> x.getId() == id)
                .findAny();
    }

    public Optional<AbandonedPlaceDto> findAbandonedPlaceByName(String name) {
        return abandonedPlaceMapper.toDtoList(abandonedPlaceRepository.findAll()).stream()
                .filter(x -> x.getName().equals(name))
                .findAny();
    }

    public Optional<AbandonedPlaceDto> updateAbandonedPlace(long id, AbandonedPlaceDto updatedAbandonedPlace) {
        Optional<AbandonedPlaceDto> abandonedPlace = this.findAbandonedPlaceById(id);
        abandonedPlace.ifPresent(existingAbandonedPlace -> {
            existingAbandonedPlace.setName(updatedAbandonedPlace.getName());
            existingAbandonedPlace.setLocation(updatedAbandonedPlace.getLocation());
            existingAbandonedPlace.setType(updatedAbandonedPlace.getType());
            existingAbandonedPlace.setCondition(updatedAbandonedPlace.getCondition());
            abandonedPlaceRepository.save(abandonedPlaceMapper.toEntity(existingAbandonedPlace));
        });
        return abandonedPlace;
    }

    public Optional<AbandonedPlaceDto> removeAbandonedPlace(long id) {
        Optional<AbandonedPlaceDto> abandonedPlace = this.findAbandonedPlaceById(id);
        abandonedPlace.ifPresent(deletedAbandonedPlace -> {
            abandonedPlaceRepository.delete(abandonedPlaceMapper.toEntity(deletedAbandonedPlace));
        });
        return abandonedPlace;
    }

}
