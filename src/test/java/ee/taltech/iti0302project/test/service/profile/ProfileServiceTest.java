package ee.taltech.iti0302project.test.service.profile;

import ee.taltech.iti0302project.app.criteria.UserCriteria;
import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.dto.profile.ChangeEmailDto;
import ee.taltech.iti0302project.app.dto.profile.ChangePasswordDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.pagination.PageResponse;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.service.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileService profileService;

    private UUID userId;
    private UserEntity userEntity;
    private UserProfileDto userProfileDto;

    private ChangeEmailDto changeEmailDto;
    private ChangePasswordDto changePasswordDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("John");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("password123");
        userEntity.setRole("admin");

        userProfileDto = UserProfileDto.builder()
                .email(userEntity.getEmail())
                .build();

        changeEmailDto = ChangeEmailDto.builder()
                .password("password123")
                .newEmail("new@example.com")
                .build();

        changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("password123")
                .newPassword("newPassword123")
                .build();
    }

    @Test
    void getUserProfile_success() {
        // Given
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userMapper.toUserProfileDto(userEntity)).willReturn(userProfileDto);

        // When
        UserProfileDto result = profileService.getUserProfile(userId);

        // Then
        assertEquals(userProfileDto, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserProfile_userNotFound() {
        // Given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When / Then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            profileService.getUserProfile(userId);
        });
        assertEquals(ProfileService.USER_NOT_FOUND_MSG, exception.getMessage());
    }

    @Test
    void updateEmail_success() {
        // Given
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(passwordEncoder.matches(changeEmailDto.getPassword(), userEntity.getPassword())).willReturn(true);
        userEntity.setEmail(changeEmailDto.getNewEmail());
        given(userRepository.save(userEntity)).willReturn(userEntity);
        given(userMapper.toUserProfileDto(userEntity)).willReturn(userProfileDto);

        // When
        UserProfileDto result = profileService.updateEmail(userId, changeEmailDto);

        // Then
        assertEquals(userProfileDto, result);
        assertEquals("new@example.com", userEntity.getEmail());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void updateEmail_invalidPassword() {
        // Given
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(passwordEncoder.matches(changeEmailDto.getPassword(), userEntity.getPassword())).willReturn(false);

        // When / Then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            profileService.updateEmail(userId, changeEmailDto);
        });
        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void updateEmail_userNotFound() {
        // Given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When / Then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            profileService.updateEmail(userId, changeEmailDto);
        });
        assertEquals(ProfileService.USER_NOT_FOUND_MSG, exception.getMessage());
    }

    @Test
    void updatePassword_success() {
        // Given
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(passwordEncoder.matches(changePasswordDto.getCurrentPassword(), userEntity.getPassword())).willReturn(true);
        given(passwordEncoder.encode(changePasswordDto.getNewPassword())).willReturn("encodedNewPassword");
        given(userRepository.save(userEntity)).willReturn(userEntity);
        given(userMapper.toUserProfileDto(userEntity)).willReturn(userProfileDto);

        // When
        UserProfileDto result = profileService.updatePassword(userId, changePasswordDto);

        // Then
        assertEquals(userProfileDto, result);
        assertEquals("encodedNewPassword", userEntity.getPassword());
        verify(userRepository, times(1)).save(userEntity);
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void updatePassword_incorrectCurrentPassword() {
        // Given
        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(passwordEncoder.matches(changePasswordDto.getCurrentPassword(), userEntity.getPassword())).willReturn(false);

        // When / Then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            profileService.updatePassword(userId, changePasswordDto);
        });
        assertEquals("Current password is incorrect", exception.getMessage());
    }

    @Test
    void updatePassword_userNotFound() {
        // Given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When / Then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            profileService.updatePassword(userId, changePasswordDto);
        });
        assertEquals(ProfileService.USER_NOT_FOUND_MSG, exception.getMessage());
    }

    @Test
    void findUsers_success() {
        // Given
        UserCriteria criteria = new UserCriteria(
                null,
                "John",
                null,
                null,
                null,
                0,
                10
        );
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserEntity> userPage = mock(Page.class);
        List<UserEntity> userEntities = List.of(userEntity);

        given(userRepository.findAll(any(Specification.class), eq(pageable))).willReturn(userPage);
        given(userPage.getContent()).willReturn(userEntities);
        given(userPage.getTotalElements()).willReturn(1L);
        given(userMapper.toUserProfileDtoList(userEntities)).willReturn(List.of(UserProfileDto.builder().username("John").build()));

        // When
        PageResponse<UserProfileDto> result = profileService.findUsers(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals("John", result.content().getFirst().getUsername());

        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findUsers_noResults() {
        // Given
        UserCriteria criteria = new UserCriteria(
                null,
                "nonexistent",
                null,
                null,
                null,
                0,
                10
        );

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserEntity> userPage = mock(Page.class);
        List<UserEntity> userEntities = List.of();

        given(userRepository.findAll(any(Specification.class), eq(pageable))).willReturn(userPage);
        given(userPage.getContent()).willReturn(userEntities);
        given(userPage.getTotalElements()).willReturn(0L);

        // When
        PageResponse<UserProfileDto> result = profileService.findUsers(criteria);

        // Then
        assertNotNull(result);
        assertEquals(0, result.totalElements());
        assertTrue(result.content().isEmpty());

        ArgumentCaptor<Specification<UserEntity>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(userRepository, times(1)).findAll(specCaptor.capture(), eq(pageable));

        Specification<UserEntity> capturedSpec = specCaptor.getValue();
        assertNotNull(capturedSpec);
    }

    @Test
    void findUsers_invalidCriteria() {
        // Given
        UserCriteria criteria = new UserCriteria(
                null,
                null,
                "invalidRole",
                null,
                null,
                0,
                10
        );
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserEntity> userPage = mock(Page.class);
        List<UserEntity> userEntities = List.of();

        given(userRepository.findAll(any(Specification.class), eq(pageable))).willReturn(userPage);
        given(userPage.getContent()).willReturn(userEntities);
        given(userPage.getTotalElements()).willReturn(0L);

        // When
        PageResponse<UserProfileDto> result = profileService.findUsers(criteria);

        // Then
        assertNotNull(result);
        assertEquals(0, result.totalElements());
        assertTrue(result.content().isEmpty());

        ArgumentCaptor<Specification<UserEntity>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(userRepository, times(1)).findAll(specCaptor.capture(), eq(pageable));

        Specification<UserEntity> capturedSpec = specCaptor.getValue();
        assertNotNull(capturedSpec);
    }

    @Test
    void findUsers_specificationTest() {
        // Given
        UserCriteria criteria = new UserCriteria(
                null,
                "John",
                "admin",
                null,
                null,
                0,
                10
        );
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<UserEntity> userPage = mock(Page.class);
        List<UserEntity> userEntities = List.of(userEntity);

        given(userRepository.findAll(any(Specification.class), eq(pageable))).willReturn(userPage);
        given(userPage.getContent()).willReturn(userEntities);
        given(userPage.getTotalElements()).willReturn(1L);
        given(userMapper.toUserProfileDtoList(userEntities)).willReturn(
                List.of(UserProfileDto.builder().username("John").role("admin").build()));

        // When
        PageResponse<UserProfileDto> result = profileService.findUsers(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.content().size());
        assertEquals("John", result.content().getFirst().getUsername());
        assertEquals("admin", result.content().getFirst().getRole());

        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

}

