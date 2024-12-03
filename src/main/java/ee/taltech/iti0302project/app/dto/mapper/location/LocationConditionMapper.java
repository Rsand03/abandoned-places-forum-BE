package ee.taltech.iti0302project.app.dto.mapper.location;

import ee.taltech.iti0302project.app.dto.location.attributes.LocationConditionDto;
import ee.taltech.iti0302project.app.entity.location.LocationConditionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationConditionMapper {

    LocationConditionDto toDto(LocationConditionEntity locationConditionEntity);

    List<LocationConditionDto> toDtoList(List<LocationConditionEntity> locationConditionEntityList);

}
