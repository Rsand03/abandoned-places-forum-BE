package ee.taltech.iti0302project.app.service.profile;

import ee.taltech.iti0302project.app.criteria.UserCriteria;
import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.dto.profile.ChangeEmailDto;
import ee.taltech.iti0302project.app.dto.profile.ChangePasswordDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.pagination.PageResponse;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.specifications.UserSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileService {

    public static final String USER_NOT_FOUND_MSG = "User not found";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND_MSG));
        return userMapper.toUserProfileDto(user);
    }

    public UserProfileDto updateEmail(UUID userId, ChangeEmailDto changeEmailDto) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND_MSG));

        if (!passwordEncoder.matches(changeEmailDto.getPassword(), user.getPassword())) {
            throw new ApplicationException("Invalid password");
        }

        user.setEmail(changeEmailDto.getNewEmail());
        userRepository.save(user);

        log.info("User {} updated email to {}", user.getId(), user.getEmail());

        return userMapper.toUserProfileDto(user);
    }

    public UserProfileDto updatePassword(UUID userId, ChangePasswordDto changePasswordDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(USER_NOT_FOUND_MSG));

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new ApplicationException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        log.info("User {} updated password", user.getId());

        return userMapper.toUserProfileDto(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserProfileDto> findUsers(UserCriteria criteria) {
        Specification<UserEntity> spec = addSpecifications(criteria);

        log.info("Search Criteria: {}", criteria);

        String sortBy = criteria.sortBy() != null ? criteria.sortBy() : "id";
        String direction = criteria.sortDirection() != null ? criteria.sortDirection().toUpperCase() : "DESC";
        int pageNumber = criteria.page() != null ? criteria.page() : 0;
        int pageSize = criteria.pageSize() != null ? criteria.pageSize() : 10;

        Sort sort = Sort.by(Sort.Direction.valueOf(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<UserEntity> entityPage = userRepository.findAll(spec, pageable);

        log.info("User Page: {}", entityPage.getContent());

        List<UserProfileDto> dtoList = userMapper.toUserProfileDtoList(entityPage.getContent());

        return new PageResponse<>(
                dtoList,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    private Specification<UserEntity> addSpecifications(UserCriteria criteria) {
        Specification<UserEntity> spec = Specification.where(null);

        if (criteria.minPoints() != null) {
            spec = spec.and(UserSpecifications.hasMinPoints(criteria.minPoints()));
        }

        if (criteria.username() != null && !criteria.username().isEmpty()) {
            spec = spec.and(UserSpecifications.hasUsername(criteria.username()));
        }

        if (criteria.role() != null && !criteria.role().isEmpty()) {
            spec = spec.and(UserSpecifications.hasRole(criteria.role()));
        }

        return spec;
    }
}
