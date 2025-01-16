package ee.taltech.iti0302project.app.dto.mapper.feed;

import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "location.id", target = "locationId")
    CreatePostDto toDto(PostEntity postEntity);

    List<CreatePostDto> toDtoList(List<PostEntity>postEntityList);

    @Mapping(target = "location", expression = "java(null)")
    PostEntity toEntity(CreatePostDto createPostDto);
}
