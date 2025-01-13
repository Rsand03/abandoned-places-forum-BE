package ee.taltech.iti0302project.test.service.auth;

import ee.taltech.iti0302project.app.dto.auth.AuthResponseDto;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.exception.UnauthorizedException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;


    private UserRegisterDto registerDto;
    private UserLoginDto loginDto;
    private AuthResponseDto authResponseDto;


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

        authResponseDto = AuthResponseDto.builder()
                .token("this.is.jwt")
                .userId(UUID.randomUUID())
                .username(registerDto.getUsername())
                .role("USER")
                .points(0)
                .build();

    }

    @Test
    void registerUser_success() {
        // Given
        given(userRepository.existsByUsername(registerDto.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(registerDto.getEmail())).willReturn(false);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerDto.getUsername());
        userEntity.setPassword(registerDto.getPassword());
        userEntity.setEmail(registerDto.getEmail());

        given(userMapper.toEntity(registerDto)).willReturn(userEntity);
        given(userRepository.save(userEntity)).willReturn(userEntity);
        given(userMapper.toAuthResponseDto(userEntity)).willReturn(authResponseDto);
        given(jwtService.generateJwt(authResponseDto)).willReturn(authResponseDto.getToken());

        // When
        AuthResponseDto result = authService.registerUser(registerDto);

        // Then
        assertEquals(result.getUsername(), registerDto.getUsername());
        then(userRepository).should(times(1)).save(userEntity);
        then(userRepository).shouldHaveNoMoreInteractions();
        then(passwordEncoder).should(times(1)).encode(registerDto.getPassword());
        then(passwordEncoder).shouldHaveNoMoreInteractions();
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
    void authenticateUser_success() {
        // Given
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(loginDto.getUsername());
        userEntity.setPassword("$2a$10$HashedPasswordInDB");

        given(userRepository.findByUsername(loginDto.getUsername())).willReturn(Optional.of(userEntity));
        given(passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())).willReturn(true);
        given(userMapper.toAuthResponseDto(userEntity)).willReturn(authResponseDto);
        given(jwtService.generateJwt(authResponseDto)).willReturn(authResponseDto.getToken());

        // When
        AuthResponseDto result = authService.authenticateUser(loginDto);

        // Then
        assertEquals(result.getUsername(), registerDto.getUsername());
        then(userRepository).should(times(1)).findByUsername(loginDto.getUsername());
        then(userRepository).shouldHaveNoMoreInteractions();
        then(passwordEncoder).should(times(1))
                .matches(loginDto.getPassword(), userEntity.getPassword());
        then(passwordEncoder).shouldHaveNoMoreInteractions();
    }

    @Test
    void authenticateUser_usernameNotFound_errorThrown() {
        // Given
        given(userRepository.findByUsername(loginDto.getUsername())).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> authService.authenticateUser(loginDto));

        // Then
        assertThat(thrown)
                .isInstanceOf(UnauthorizedException.class)
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
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Incorrect username or password");
        then(userRepository).shouldHaveNoMoreInteractions();
    }

}
