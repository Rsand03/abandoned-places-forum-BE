package ee.taltech.iti0302project.test.service.auth;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;


    private UserRegisterDto registerDto;
    private UserLoginDto loginDto;


    @BeforeEach
    void setup() {
        registerDto = UserRegisterDto.builder()
                .username("valid username")
                .password("validPassword")
                .email("valid@taltech.ee")
                .build();

        loginDto = UserLoginDto.builder()
                .username("user")
                .password("user")
                .build();
    }

    @Test
    void registerUser_usernameAlreadyInUse_errorThrown() {
        // Given
        given(userRepository.existsByUsername(registerDto.getUsername())).willReturn(true);

        // When
        Throwable thrown = catchThrowable(() -> authService.registerUser(registerDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Username is already in use.");
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void registerUser_emailAlreadyInUse_errorThrown() {
        // Given
        given(userRepository.existsByUsername(registerDto.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(registerDto.getEmail())).willReturn(true);

        // When
        Throwable thrown = catchThrowable(() -> authService.registerUser(registerDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Email is already in use.");
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void authenticateUser_usernameNotFound_errorThrown() {
        // Given
        given(userRepository.findByUsername(loginDto.getUsername())).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> authService.authenticateUser(loginDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Incorrect username or password");
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void authenticateUser_passwordDoesNotMatch_errorThrown() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("otherPassword");
        given(userRepository.findByUsername(loginDto.getUsername())).willReturn(Optional.of(userEntity));

        // When
        Throwable thrown = catchThrowable(() -> authService.authenticateUser(loginDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(ApplicationException.class)
                .hasMessage("Incorrect username or password");
        then(userRepository).shouldHaveNoMoreInteractions();
    }

}
