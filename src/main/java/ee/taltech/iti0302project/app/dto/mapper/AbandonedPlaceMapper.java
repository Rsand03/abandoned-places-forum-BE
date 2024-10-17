package ee.taltech.iti0302project.app.dto.mapper;

import ee.taltech.iti0302project.app.dto.AbandonedPlaceDto;
import ee.taltech.iti0302project.app.entity.AbandonedPlaceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AbandonedPlaceMapper {

    AbandonedPlaceDto toDto(AbandonedPlaceEntity abandonedPlaceEntity);

    List<AbandonedPlaceDto> toDtoList(List<AbandonedPlaceEntity> abandonedPlaceEntityList);

    AbandonedPlaceEntity toEntity(AbandonedPlaceDto abandonedPlaceDto);

}
