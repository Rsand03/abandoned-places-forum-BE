package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationConditionDto;
import ee.taltech.iti0302project.app.dto.mapper.LocationConditionMapper;
import ee.taltech.iti0302project.app.repository.location.LocationConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LocationConditionService {

    private final LocationConditionRepository locationConditionRepository;
    private final LocationConditionMapper locationConditionMapper;

    public List<LocationConditionDto> getAllLocationConditions() {
        return locationConditionMapper.toDtoList(locationConditionRepository.findAll());
    }

}
