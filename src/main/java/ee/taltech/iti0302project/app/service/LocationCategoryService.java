package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.LocationCategoryDto;
import ee.taltech.iti0302project.app.dto.mapper.LocationCategoryMapper;
import ee.taltech.iti0302project.app.repository.LocationCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LocationCategoryService {

    private final LocationCategoryRepository locationCategoryRepository;
    private final LocationCategoryMapper locationCategoryMapper;

    public List<LocationCategoryDto> getAllLocationCategories() {
        return locationCategoryMapper.toDtoList(locationCategoryRepository.findAll());
    }

}
