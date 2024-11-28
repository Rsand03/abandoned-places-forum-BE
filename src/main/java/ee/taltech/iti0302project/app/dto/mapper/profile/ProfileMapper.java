package ee.taltech.iti0302project.app.dto.mapper.profile;

import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {
    UserEntity toEntity(UserRegisterDto userRegisterDto);

    UserRegisterDto toDto(UserEntity userEntity);

    UserProfileDto toUserProfileDto(UserEntity user);
}

