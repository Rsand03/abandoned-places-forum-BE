package ee.taltech.iti0302project.app.dto.mapper.user;

import ee.taltech.iti0302project.app.dto.UserDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserEntity toEntity(UserRegisterDto userRegisterDto);

    UserRegisterDto toDto(UserEntity userEntity);

    List<UserDto> toDtoList(List<UserEntity> userEntityList);
}
