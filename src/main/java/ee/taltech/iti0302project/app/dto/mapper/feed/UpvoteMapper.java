package ee.taltech.iti0302project.app.dto.mapper.feed;

import ee.taltech.iti0302project.app.dto.feed.CreateUpvoteDto;
import ee.taltech.iti0302project.app.dto.feed.UpvoteResponseDto;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpvoteMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "user.id", target = "userId")
    UpvoteResponseDto toResponseDto(UpvoteEntity upvoteEntity);

    List<UpvoteResponseDto> toDtoList(List<UpvoteEntity>upvoteEntityList);

    UpvoteEntity toEntity(CreateUpvoteDto upvoteDto);

}
