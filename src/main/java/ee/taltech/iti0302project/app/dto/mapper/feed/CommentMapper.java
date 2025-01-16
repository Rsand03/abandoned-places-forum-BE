package ee.taltech.iti0302project.app.dto.mapper.feed;

import ee.taltech.iti0302project.app.dto.feed.CommentDto;
import ee.taltech.iti0302project.app.dto.feed.CreateCommentDto;
import ee.taltech.iti0302project.app.entity.feed.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "createdBy.username", target = "createdByUsername")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(CommentEntity commentEntity);

    @Mapping(source = "createdBy.username", target = "createdByUsername")
    List<CommentDto> toDtoList(List<CommentEntity> commentEntityList);

    CommentEntity toEntity(CreateCommentDto commentDto);
}
