package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.LocationMapper;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public List<LocationResponseDto> getAllLocations() {
        return locationMapper.toDtoList(locationRepository.findAll());
    }

}
