package ee.taltech.iti0302project.app.dto.mapper;

import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper {

    @Mapping(source = "condition.name", target = "condition")
    @Mapping(source = "status.name", target = "status")
    LocationResponseDto toResponseDto(LocationEntity locationEntity);

    List<LocationResponseDto> toDtoList(List<LocationEntity> locationEntityList);

    @Mapping(source = "createdByUserUuid", target = "createdBy")
    LocationEntity toEntity(LocationCreateDto locationCreateDto);

}
