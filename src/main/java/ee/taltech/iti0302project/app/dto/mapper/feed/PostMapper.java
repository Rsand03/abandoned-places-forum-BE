package ee.taltech.iti0302project.app.dto.mapper.feed;

import ee.taltech.iti0302project.app.dto.feed.PostDto;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostDto toDto(PostEntity postEntity);

    List<PostDto> toDtoList(List<PostEntity>postEntityList);

    PostEntity toEntity(PostDto postDto);
}
