package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.UserCriteria;
import ee.taltech.iti0302project.app.dto.UserDto;
import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.UserSpecifications;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRespoitory;
    private UserMapper userMapper;

    public Page<UserDto> findUsers(UserCriteria criteria) {
        Specification<UserEntity> spec = Specification.where(null);
        if (criteria.getMinPoints() != null) {
            spec = spec.and(UserSpecifications.hasMorePointsThan(criteria.getMinPoints()));
        }
        Sort sort = Sort.by(Sort.Direction.valueOf("DESC"), "username");
        Pageable pageable = PageRequest.of(0, 20, sort);

        Page<UserEntity> userPage = userRespoitory.findAll(spec, pageable);
        return toUserDtoPage(userPage);
    }

    private Page<UserDto> toUserDtoPage(Page<UserEntity> userEntityPage) {
        List<UserDto> userDtos = userMapper.toDtoList(userEntityPage.getContent());

        return new PageImpl<>(userDtos, userEntityPage.getPageable(), userEntityPage.getTotalElements());
    }
}

