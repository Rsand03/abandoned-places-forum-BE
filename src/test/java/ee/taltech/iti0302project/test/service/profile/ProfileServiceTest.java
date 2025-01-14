package ee.taltech.iti0302project.test.service.profile;

import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.dto.profile.ChangeEmailDto;
import ee.taltech.iti0302project.app.dto.profile.ChangePasswordDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.service.profile.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("password123");

        userProfileDto = new UserProfileDto();
        userProfileDto.setEmail(userEntity.getEmail());

        changeEmailDto = new ChangeEmailDto();
        changeEmailDto.setPassword("password123");
        changeEmailDto.setNewEmail("new@example.com");

        changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentPassword("password123");
        changePasswordDto.setNewPassword("newPassword123");
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
}

