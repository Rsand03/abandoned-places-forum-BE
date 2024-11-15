package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.LocationTypeDto;
import ee.taltech.iti0302project.app.dto.mapper.LocationTypeMapper;
import ee.taltech.iti0302project.app.repository.LocationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationTypeService {

    private final LocationTypeRepository locationTypeRepository;
    private final LocationTypeMapper locationTypeMapper;

    public List<LocationTypeDto> getAllLocationTypes() {
        return locationTypeMapper.toDtoList(locationTypeRepository.findAll());
    }

}
