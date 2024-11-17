package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationStatusDto;
import ee.taltech.iti0302project.app.dto.mapper.LocationStatusMapper;
import ee.taltech.iti0302project.app.repository.location.LocationStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LocationStatusService {

    private final LocationStatusRepository locationStatusRepository;
    private final LocationStatusMapper locationStatusMapper;

    public List<LocationStatusDto> getAllLocationStatuses() {
        return locationStatusMapper.toDtoList(locationStatusRepository.findAll());
    }

}
