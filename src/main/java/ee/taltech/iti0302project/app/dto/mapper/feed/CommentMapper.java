package ee.taltech.iti0302project.app.dto.mapper.feed;

import ee.taltech.iti0302project.app.dto.feed.CommentDto;
import ee.taltech.iti0302project.app.entity.feed.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "createdBy.username", target = "createdByUsername")
    CommentDto toDto(CommentEntity commentEntity);

    @Mapping(source = "createdBy.username", target = "createdByUsername")
    List<CommentDto> toDtoList(List<CommentEntity> commentEntityList);

    CommentEntity toEntity(CommentDto commentDto);
}
