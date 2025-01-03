package ee.taltech.iti0302project.app.service.auth;

import ee.taltech.iti0302project.app.dto.auth.AuthResponseDto;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.exception.AuthException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class AuthService {

    private static final int JWT_EXPIRATION_TIME_MILLISECONDS = 1000 * 60 * 60 * 24;

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey key;


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

        userRepository.save(user);

        String token = generateJwtToken(user);
        AuthResponseDto authResponseDto = userMapper.toAuthResponseDto(user);
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
                    throw new ApplicationException("Incorrect username or password");
                });

        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            log.info("Authentication failed: Incorrect password for user {}", userLoginDto.getUsername());
            throw new ApplicationException("Incorrect username or password");
        }

        String token = generateJwtToken(user);
        AuthResponseDto authResponseDto = userMapper.toAuthResponseDto(user);
        authResponseDto.setToken(token);

        log.info("User authenticated successfully with username: {}", userLoginDto.getUsername());
        return authResponseDto;
    }

    private String generateJwtToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of(
                        "userId", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "points", user.getPoints()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME_MILLISECONDS))
                .signWith(key)
                .compact();
    }

    public UUID extractUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);  // Remove "Bearer " prefix

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return UUID.fromString(claims.get("userId", String.class));
        } catch (Exception e) {
            throw new AuthException("Invalid or expired token");
        }
    }

}
