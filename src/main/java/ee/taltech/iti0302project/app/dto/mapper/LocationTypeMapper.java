package ee.taltech.iti0302project.app.dto.mapper;

import ee.taltech.iti0302project.app.dto.LocationTypeDto;
import ee.taltech.iti0302project.app.entity.LocationTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationTypeMapper {

    LocationTypeDto toDto(LocationTypeEntity locationTypeEntity);

    List<LocationTypeDto> toDtoList(List<LocationTypeEntity> locationTypeEntityList);

    LocationTypeEntity toEntity(LocationTypeDto locationTypeDto);

}
