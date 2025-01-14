package ee.taltech.iti0302project.app.dto.mapper.location;

import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkCreateDto;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkDto;
import ee.taltech.iti0302project.app.entity.location.LocationBookmarkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationBookmarkMapper {
    @Mapping(source = "createdByUserUuid", target = "createdBy")
    @Mapping(source = "locationId", target = "location.id")
    @Mapping(source = "type", target = "type")
    LocationBookmarkEntity toEntity(LocationBookmarkCreateDto locationBookmarkCreateDto);

    @Mapping(source = "type", target = "type")
    LocationBookmarkDto toResponseDto(LocationBookmarkEntity locationBookmarkEntity);
}
