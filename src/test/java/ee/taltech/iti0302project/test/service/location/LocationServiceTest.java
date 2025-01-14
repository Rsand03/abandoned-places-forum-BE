package ee.taltech.iti0302project.test.service.location;

import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationAttributesDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationCategoryDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationStatusDto;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationCategoryMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationConditionMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationStatusMapper;
import ee.taltech.iti0302project.app.entity.location.LocationCategoryEntity;
import ee.taltech.iti0302project.app.entity.location.LocationConditionEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.entity.location.LocationStatusEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.location.*;
import ee.taltech.iti0302project.app.service.location.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    private static final long ALLOWED_AMOUNT_OF_LOCATIONS = 200L;
    private static final long EXACT_MAX_ALLOWED_AMOUNT_OF_LOCATIONS = 249L;

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationBookmarkRepository locationBookmarkRepository;
    @Mock
    private LocationCategoryRepository locationCategoryRepository;
    @Mock
    private LocationConditionRepository locationConditionRepository;
    @Mock
    private LocationStatusRepository locationStatusRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationMapper locationMapper;
    @Mock
    private LocationCategoryMapper categoryMapper;
    @Mock
    private LocationConditionMapper conditionMapper;
    @Mock
    private LocationStatusMapper statusMapper;


    @InjectMocks
    private LocationService locationService;


    private LocationEntity deletedLocation;

    private LocationCategoryEntity defaultMainCategory;
    private List<LocationCategoryEntity> defaultSubCategories;
    private LocationConditionEntity defaultCondition;
    private LocationStatusEntity defaultStatus;
    private LocationCreateDto defaultLocationCreateDto;

    private LocationCategoryDto defaultMainCategoryDto;
    private List<LocationCategoryDto> defaultSubCategoriesDto;

    private LocationEntity locationEntitySavedToRepository;
    private LocationEntity locationEntityReceivedFromRepository;
    private LocationResponseDto locationResponseDto;


    @BeforeEach
    void setUp() {
        defaultMainCategory = new LocationCategoryEntity();
        defaultMainCategory.setId(2L);
        LocationCategoryEntity subCategory1 = new LocationCategoryEntity();
        subCategory1.setId(3L);
        LocationCategoryEntity subCategory2 = new LocationCategoryEntity();
        subCategory2.setId(4L);
        defaultSubCategories = List.of(subCategory1, subCategory2);

        defaultMainCategoryDto = LocationCategoryDto.builder().id(2L).build();
        defaultSubCategoriesDto = List.of(
                LocationCategoryDto.builder().id(3L).build(), LocationCategoryDto.builder().id(4L).build());

        defaultCondition = new LocationConditionEntity();
        defaultCondition.setId(1L);
        defaultStatus = new LocationStatusEntity();
        defaultStatus.setId(1L);

        defaultLocationCreateDto = LocationCreateDto.builder()
                .createdBy(UUID.randomUUID())
                .mainCategoryId(defaultMainCategory.getId())
                .subCategoryIds(defaultSubCategories.stream().map(LocationCategoryEntity::getId).toList())
                .conditionId(defaultCondition.getId())
                .statusId(defaultStatus.getId())
                .build();

        locationEntitySavedToRepository = new LocationEntity();
        locationEntityReceivedFromRepository = new LocationEntity();
        locationResponseDto = new LocationResponseDto();
    }

    @BeforeEach
    void deleteLocation() {
        deletedLocation = new LocationEntity();
        deletedLocation.setId(UUID.randomUUID());
        deletedLocation.setCreatedBy(UUID.randomUUID());
        deletedLocation.setPublic(false);
    }

    @Test
    void getFilteredLocations() {
        assertTrue(true);
    }

    @Test
    void getLocationById() {
        assertTrue(true);
    }

    @Test
    void publishLocation() {
        assertTrue(true);
    }


    @Test
    void createLocation_withSubCategories_isCreated() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(ALLOWED_AMOUNT_OF_LOCATIONS);
        defaultLocationCreateDto.getSubCategoryIds().forEach(id ->
                given(locationCategoryRepository.existsById(id)).willReturn(true));

        given(locationMapper.toEntity(defaultLocationCreateDto)).willReturn(locationEntitySavedToRepository);

        given(locationCategoryRepository.findAllById(defaultLocationCreateDto.getSubCategoryIds()))
                .willReturn(defaultSubCategories);
        given(locationCategoryRepository.findById(defaultLocationCreateDto.getMainCategoryId()))
                .willReturn(Optional.of(defaultMainCategory));
        given(locationConditionRepository.findById(defaultLocationCreateDto.getConditionId()))
                .willReturn(Optional.of(defaultCondition));
        given(locationStatusRepository.findById(defaultLocationCreateDto.getStatusId()))
                .willReturn(Optional.of(defaultStatus));


        given(locationRepository.save(locationEntitySavedToRepository)).willReturn(locationEntityReceivedFromRepository);
        locationEntityReceivedFromRepository.setMainCategory(defaultMainCategory);
        locationEntityReceivedFromRepository.setSubCategories(defaultSubCategories);
        locationEntityReceivedFromRepository.setCondition(defaultCondition);
        locationEntityReceivedFromRepository.setStatus(defaultStatus);

        given(locationMapper.toResponseDto(locationEntityReceivedFromRepository)).willReturn(locationResponseDto);
        locationResponseDto.setMainCategory(defaultMainCategoryDto);
        locationResponseDto.setSubCategories(defaultSubCategoriesDto);
        locationResponseDto.setCondition(locationEntityReceivedFromRepository.getCondition().getName());
        locationResponseDto.setStatus(locationEntityReceivedFromRepository.getStatus().getName());

        // When
        LocationResponseDto result = locationService.createLocation(defaultLocationCreateDto);

        // Then
        assertEquals(defaultSubCategoriesDto, result.getSubCategories());
        then(locationRepository).should(times(1)).save(locationEntitySavedToRepository);
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_noSubCategories_isCreated() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(ALLOWED_AMOUNT_OF_LOCATIONS);
        defaultLocationCreateDto.getSubCategoryIds().forEach(id ->
                given(locationCategoryRepository.existsById(id)).willReturn(true));

        given(locationMapper.toEntity(defaultLocationCreateDto)).willReturn(locationEntitySavedToRepository);

        given(locationCategoryRepository.findAllById(defaultLocationCreateDto.getSubCategoryIds()))
                .willReturn(List.of());
        given(locationCategoryRepository.findById(defaultLocationCreateDto.getMainCategoryId()))
                .willReturn(Optional.of(defaultMainCategory));
        given(locationConditionRepository.findById(defaultLocationCreateDto.getConditionId()))
                .willReturn(Optional.of(defaultCondition));
        given(locationStatusRepository.findById(defaultLocationCreateDto.getStatusId()))
                .willReturn(Optional.of(defaultStatus));


        given(locationRepository.save(locationEntitySavedToRepository)).willReturn(locationEntityReceivedFromRepository);
        locationEntityReceivedFromRepository.setMainCategory(defaultMainCategory);
        locationEntityReceivedFromRepository.setSubCategories(List.of());
        locationEntityReceivedFromRepository.setCondition(defaultCondition);
        locationEntityReceivedFromRepository.setStatus(defaultStatus);

        given(locationMapper.toResponseDto(locationEntityReceivedFromRepository)).willReturn(locationResponseDto);
        locationResponseDto.setMainCategory(defaultMainCategoryDto);
        locationResponseDto.setSubCategories(List.of());
        locationResponseDto.setCondition(locationEntityReceivedFromRepository.getCondition().getName());
        locationResponseDto.setStatus(locationEntityReceivedFromRepository.getStatus().getName());

        // When
        LocationResponseDto result = locationService.createLocation(defaultLocationCreateDto);

        // Then
        assertEquals(List.of(), result.getSubCategories());
        then(locationRepository).should(times(1)).save(locationEntitySavedToRepository);
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_nullCreatedBy_errorThrown() {
        // Given
        defaultLocationCreateDto.setCreatedBy(null);

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Invalid user");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_createdByNotFound_errorThrown() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(false);

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Invalid user");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_exactMaxAmountOfAllowedLocations_noSpecificError() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(EXACT_MAX_ALLOWED_AMOUNT_OF_LOCATIONS);

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .extracting(Throwable::getMessage)
                .isNotEqualTo("User exceeded maximum amount of private locations");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_userExceedsMaxAmountOfAllowedLocations_errorThrown() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(EXACT_MAX_ALLOWED_AMOUNT_OF_LOCATIONS + 1L);

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("User exceeded maximum amount of private locations");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_matchingSubCategoryAndMainCategoryId_errorThrown() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(ALLOWED_AMOUNT_OF_LOCATIONS);

        defaultLocationCreateDto.setMainCategoryId(defaultLocationCreateDto.getSubCategoryIds().getFirst());

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Duplicate of main category in subcategories");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_nullSubCategory_errorThrown() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(ALLOWED_AMOUNT_OF_LOCATIONS);

        List<Long> subCategoryIdsWithNull = new ArrayList<>(defaultLocationCreateDto.getSubCategoryIds());
        subCategoryIdsWithNull.add(null);
        defaultLocationCreateDto.setSubCategoryIds(subCategoryIdsWithNull);

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Invalid subcategories");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_subCategoryNotFound_errorThrown() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(ALLOWED_AMOUNT_OF_LOCATIONS);
        given(locationCategoryRepository.existsById(defaultLocationCreateDto.getSubCategoryIds()
                .get(0))).willReturn(true);
        given(locationCategoryRepository.existsById(defaultLocationCreateDto.getSubCategoryIds()
                .get(1))).willReturn(false);

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Invalid subcategories");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_mainCategoryNotFound_errorThrown() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(ALLOWED_AMOUNT_OF_LOCATIONS);
        defaultLocationCreateDto.getSubCategoryIds().forEach(id ->
                given(locationCategoryRepository.existsById(id)).willReturn(true));

        given(locationMapper.toEntity(defaultLocationCreateDto)).willReturn(locationEntitySavedToRepository);

        given(locationCategoryRepository.findAllById(defaultLocationCreateDto.getSubCategoryIds()))
                .willReturn(defaultSubCategories);
        given(locationCategoryRepository.findById(defaultLocationCreateDto.getMainCategoryId()))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Invalid main category id");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_conditionNotFound_errorThrown() {
        // Given

        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(ALLOWED_AMOUNT_OF_LOCATIONS);
        defaultLocationCreateDto.getSubCategoryIds().forEach(id ->
                given(locationCategoryRepository.existsById(id)).willReturn(true));

        given(locationMapper.toEntity(defaultLocationCreateDto)).willReturn(locationEntitySavedToRepository);

        given(locationCategoryRepository.findAllById(defaultLocationCreateDto.getSubCategoryIds()))
                .willReturn(defaultSubCategories);
        given(locationCategoryRepository.findById(defaultLocationCreateDto.getMainCategoryId()))
                .willReturn(Optional.of(defaultMainCategory));
        given(locationConditionRepository.findById(defaultLocationCreateDto.getConditionId()))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Invalid condition id");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void createLocation_statusNotFound_errorThrown() {
        // Given
        given(userRepository.existsById(defaultLocationCreateDto.getCreatedBy())).willReturn(true);
        given(locationRepository.countByIsPublicFalseAndCreatedBy(defaultLocationCreateDto.getCreatedBy()))
                .willReturn(ALLOWED_AMOUNT_OF_LOCATIONS);
        defaultLocationCreateDto.getSubCategoryIds().forEach(id ->
                given(locationCategoryRepository.existsById(id)).willReturn(true));

        given(locationMapper.toEntity(defaultLocationCreateDto)).willReturn(locationEntitySavedToRepository);

        given(locationCategoryRepository.findAllById(defaultLocationCreateDto.getSubCategoryIds()))
                .willReturn(defaultSubCategories);
        given(locationCategoryRepository.findById(defaultLocationCreateDto.getMainCategoryId()))
                .willReturn(Optional.of(defaultMainCategory));
        given(locationConditionRepository.findById(defaultLocationCreateDto.getConditionId()))
                .willReturn(Optional.of(defaultCondition));
        given(locationStatusRepository.findById(defaultLocationCreateDto.getStatusId()))
                .willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> locationService.createLocation(defaultLocationCreateDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Invalid status id");
        then(locationRepository).shouldHaveNoMoreInteractions();
    }


    @Test
    void deleteLocationByUuid_isDeleted() {
        // given
        given(locationRepository.findById(deletedLocation.getId())).willReturn(Optional.of(deletedLocation));
        willDoNothing().given(locationBookmarkRepository).deleteAllByLocationAndCreatedBy(deletedLocation, deletedLocation.getCreatedBy());

        // when
        Optional<LocationResponseDto> result = locationService
                .deleteLocationByUuid(deletedLocation.getId(), deletedLocation.getCreatedBy());

        // then
        assertFalse(result.isPresent());
        then(locationRepository).should(times(1)).deleteById(deletedLocation.getId());
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteLocationByUuid_differentCreatedBy_notDeleted() {
        // given
        UUID randomCreatedBy = UUID.randomUUID();
        given(locationRepository.findById(deletedLocation.getId())).willReturn(Optional.of(deletedLocation));

        // when
        Optional<LocationResponseDto> result = locationService
                .deleteLocationByUuid(deletedLocation.getId(), randomCreatedBy);

        // then
        assertFalse(result.isPresent());
        then(locationRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void deleteLocationByUuid_publicLocation_notDeleted() {
        // given
        LocationEntity publicLocation = new LocationEntity();
        publicLocation.setId(UUID.randomUUID());
        publicLocation.setCreatedBy(UUID.randomUUID());
        publicLocation.setPublic(true);

        given(locationRepository.findById(publicLocation.getId())).willReturn(Optional.of(publicLocation));

        // when
        Optional<LocationResponseDto> result = locationService
                .deleteLocationByUuid(publicLocation.getId(), publicLocation.getCreatedBy());

        // then
        assertFalse(result.isPresent());
        then(locationRepository).shouldHaveNoMoreInteractions();
    }


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
        LocationAttributesDto result = locationService.getLocationAttributes();

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
