package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.LocationConditionDto;
import ee.taltech.iti0302project.app.dto.mapper.LocationConditionMapper;
import ee.taltech.iti0302project.app.repository.location.LocationConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationConditionService {

    private final LocationConditionRepository locationConditionRepository;
    private final LocationConditionMapper locationConditionMapper;

    public List<LocationConditionDto> getAllLocationConditions() {
        return locationConditionMapper.toDtoList(locationConditionRepository.findAll());
    }

}
