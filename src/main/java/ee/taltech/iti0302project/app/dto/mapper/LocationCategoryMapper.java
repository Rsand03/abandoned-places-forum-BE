package ee.taltech.iti0302project.app.dto.mapper;

import ee.taltech.iti0302project.app.dto.LocationCategoryDto;
import ee.taltech.iti0302project.app.entity.LocationCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationCategoryMapper {

    LocationCategoryDto toDto(LocationCategoryEntity locationCategoryEntity);

    List<LocationCategoryDto> toDtoList(List<LocationCategoryEntity> locationCategoryEntityList);

}
