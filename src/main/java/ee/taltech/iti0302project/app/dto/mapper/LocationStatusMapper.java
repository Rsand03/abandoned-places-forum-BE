package ee.taltech.iti0302project.app.dto.mapper;

import ee.taltech.iti0302project.app.dto.LocationStatusDto;
import ee.taltech.iti0302project.app.entity.LocationStatusEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationStatusMapper {

    LocationStatusDto toDto(LocationStatusEntity locationStatusEntity);

    List<LocationStatusDto> toDtoList(List<LocationStatusEntity> locationStatusEntityList);

}
