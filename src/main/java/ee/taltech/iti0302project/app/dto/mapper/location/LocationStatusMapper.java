package ee.taltech.iti0302project.app.dto.mapper.location;

import ee.taltech.iti0302project.app.dto.location.attributes.LocationStatusDto;
import ee.taltech.iti0302project.app.entity.location.LocationStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationStatusMapper {

    LocationStatusDto toDto(LocationStatusEntity locationStatusEntity);

    List<LocationStatusDto> toDtoList(List<LocationStatusEntity> locationStatusEntityList);

}
