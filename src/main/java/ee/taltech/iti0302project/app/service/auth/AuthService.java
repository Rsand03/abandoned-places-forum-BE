package ee.taltech.iti0302project.app.service.auth;

import ee.taltech.iti0302project.app.dto.auth.AuthResponseDto;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.exception.UnauthorizedException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class AuthService {


    private final JwtService jwtService;

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public AuthResponseDto registerUser(UserRegisterDto userRegisterDto) {
        log.info("Registering user with username: {}", userRegisterDto.getUsername());

        if (userRepository.existsByUsername(userRegisterDto.getUsername())) {
            throw new ApplicationException("Username is already in use.");
        }
        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            throw new ApplicationException("Email is already in use.");
        }

        UserEntity user = userMapper.toEntity(userRegisterDto);

        String hashedPassword = passwordEncoder.encode(userRegisterDto.getPassword());
        user.setPassword(hashedPassword);
        user.setPoints(0);
        user.setRole("USER");

        UserEntity savedUser = userRepository.save(user);
        AuthResponseDto authResponseDto = userMapper.toAuthResponseDto(savedUser);

        String token = jwtService.generateJwt(authResponseDto);
        authResponseDto.setToken(token);

        log.info("User registered and saved with ID: {}", user.getId());
        return authResponseDto;
    }

    @Transactional(readOnly = true)
    public AuthResponseDto authenticateUser(UserLoginDto userLoginDto) {
        log.info("Authenticating user with username: {}", userLoginDto.getUsername());

        UserEntity user = userRepository.findByUsername(userLoginDto.getUsername())
                .orElseGet(() -> {
                    log.info("User not found: " + userLoginDto.getUsername());
                    throw new UnauthorizedException("Incorrect username or password");
                });

        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            log.info("Authentication failed: Incorrect password for user {}", userLoginDto.getUsername());
            throw new UnauthorizedException("Incorrect username or password");
        }

        AuthResponseDto authResponseDto = userMapper.toAuthResponseDto(user);

        String token = jwtService.generateJwt(authResponseDto);
        authResponseDto.setToken(token);

        log.info("User authenticated successfully with username: {}", userLoginDto.getUsername());
        return authResponseDto;
    }

}
