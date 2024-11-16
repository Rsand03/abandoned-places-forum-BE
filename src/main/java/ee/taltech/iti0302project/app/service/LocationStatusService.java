package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.LocationStatusDto;
import ee.taltech.iti0302project.app.dto.mapper.LocationStatusMapper;
import ee.taltech.iti0302project.app.repository.LocationStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationStatusService {

    private final LocationStatusRepository locationStatusRepository;
    private final LocationStatusMapper locationStatusMapper;

    public List<LocationStatusDto> getAllLocationStatuses() {
        return locationStatusMapper.toDtoList(locationStatusRepository.findAll());
    }

}
