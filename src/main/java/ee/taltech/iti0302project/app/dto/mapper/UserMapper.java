package ee.taltech.iti0302project.app.dto.mapper;

import ee.taltech.iti0302project.app.dto.UserRegisterDto;
import ee.taltech.iti0302project.app.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserEntity toEntity(UserRegisterDto userRegisterDto);

    UserRegisterDto toDto(UserEntity userEntity);
}
