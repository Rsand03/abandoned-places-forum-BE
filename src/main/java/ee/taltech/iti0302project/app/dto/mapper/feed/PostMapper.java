package ee.taltech.iti0302project.app.dto.mapper.feed;

import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    CreatePostDto toDto(PostEntity postEntity);

    List<CreatePostDto> toDtoList(List<PostEntity>postEntityList);

    PostEntity toEntity(CreatePostDto createPostDto);
}
