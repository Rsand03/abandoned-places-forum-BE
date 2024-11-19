package ee.taltech.iti0302project.app.dto.mapper.feed;

import ee.taltech.iti0302project.app.dto.feed.UpvoteDto;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpvoteMapper {
    UpvoteDto toDto(UpvoteEntity upvoteEntity);

    List<UpvoteDto> toDtoList(List<UpvoteEntity>upvoteEntityList);

    UpvoteEntity toEntity(UpvoteDto upvoteDto);
}
