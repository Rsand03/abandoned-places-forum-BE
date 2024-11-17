package ee.taltech.iti0302project.app.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationCategoryDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationCategoryMapper;
import ee.taltech.iti0302project.app.repository.location.LocationCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class LocationCategoryService {

    private final LocationCategoryRepository locationCategoryRepository;
    private final LocationCategoryMapper locationCategoryMapper;

    public List<LocationCategoryDto> getAllLocationCategories() {
        return locationCategoryMapper.toDtoList(locationCategoryRepository.findAll());
    }

}
