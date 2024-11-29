package ee.taltech.iti0302project.app.dto.mapper.feed;

import ee.taltech.iti0302project.app.dto.feed.FetchPostsDto;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FetchPostsMapper {

    @Mapping(source = "createdBy.username", target = "createdByUsername")
    FetchPostsDto toDto(PostEntity postEntity);

    @Mapping(source = "createdBy.username", target = "createdByUsername")
    List<FetchPostsDto> toDtoList(List<PostEntity> postEntityList);

    PostEntity toEntity(FetchPostsDto postDto);
}

