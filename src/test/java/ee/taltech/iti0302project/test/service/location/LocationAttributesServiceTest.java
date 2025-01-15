package ee.taltech.iti0302project.test.service.location;

import ee.taltech.iti0302project.app.dto.location.attributes.LocationAttributesDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationCategoryDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationStatusDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationCategoryMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationConditionMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationStatusMapper;
import ee.taltech.iti0302project.app.entity.location.LocationCategoryEntity;
import ee.taltech.iti0302project.app.entity.location.LocationStatusEntity;
import ee.taltech.iti0302project.app.repository.location.LocationCategoryRepository;
import ee.taltech.iti0302project.app.repository.location.LocationConditionRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.repository.location.LocationStatusRepository;
import ee.taltech.iti0302project.app.service.location.LocationAttributesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class LocationAttributesServiceTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationCategoryRepository locationCategoryRepository;
    @Mock
    private LocationConditionRepository locationConditionRepository;
    @Mock
    private LocationStatusRepository locationStatusRepository;
    @Mock
    private LocationCategoryMapper categoryMapper;
    @Mock
    private LocationConditionMapper conditionMapper;
    @Mock
    private LocationStatusMapper statusMapper;


    @InjectMocks
    private LocationAttributesService locationAttributesService;


    @Test
    void getLocationAttributes() {
        // Given
        LocationCategoryEntity category1 = new LocationCategoryEntity();
        category1.setId(1L);
        LocationCategoryEntity category2 = new LocationCategoryEntity();
        category2.setId(2L);
        category2.setName("Category2");
        category2.setColorHex("33FF57");
        List<LocationCategoryEntity> categoryEntities = List.of(category1, category2);

        LocationStatusEntity status1 = new LocationStatusEntity();
        status1.setId(1L);
        List<LocationStatusEntity> statusEntities = List.of(status1);

        LocationCategoryDto categoryDto1 = LocationCategoryDto.builder().id(1L).build();
        LocationCategoryDto categoryDto2 = LocationCategoryDto.builder()
                .id(2L)
                .name("Category2")
                .colorHex("33FF57")
                .build();
        List<LocationCategoryDto> categoryDtoList = List.of(categoryDto1, categoryDto2);

        LocationStatusDto statusDto1 = LocationStatusDto.builder().id(1L).build();
        List<LocationStatusDto> statusDtoList = List.of(statusDto1);

        given(locationCategoryRepository.findAll()).willReturn(categoryEntities);
        given(locationConditionRepository.findAll()).willReturn(List.of());
        given(locationStatusRepository.findAll()).willReturn(statusEntities);

        given(categoryMapper.toDtoList(categoryEntities)).willReturn(categoryDtoList);
        given(conditionMapper.toDtoList(List.of())).willReturn(List.of());
        given(statusMapper.toDtoList(statusEntities)).willReturn(statusDtoList);

        // When
        LocationAttributesDto result = locationAttributesService.getLocationAttributes();

        // Then
        assertEquals(categoryDtoList, result.getCategories());
        assertEquals(List.of(), result.getConditions());
        assertEquals(statusDtoList, result.getStatuses());

        then(locationCategoryRepository).should(times(1)).findAll();
        then(locationConditionRepository).should(times(1)).findAll();
        then(locationStatusRepository).should(times(1)).findAll();
        then(locationRepository).shouldHaveNoMoreInteractions();
    }
}
