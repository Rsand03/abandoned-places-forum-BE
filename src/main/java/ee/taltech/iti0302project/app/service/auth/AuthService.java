package ee.taltech.iti0302project.app.service.auth;

import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.dto.auth.AuthenticationResponseDto;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecretKey key;

    public AuthenticationResponseDto registerUser(UserRegisterDto userRegisterDto) {

        logger.info("Registering user with username: {}", userRegisterDto.getUsername());

        if (userRepository.existsByUsername(userRegisterDto.getUsername())) {
            throw new IllegalArgumentException("Username is already in use.");
        }

        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(userRegisterDto.getUsername());

        String hashedPassword = passwordEncoder.encode(userRegisterDto.getPassword());
        user.setPassword(hashedPassword);

        user.setEmail(userRegisterDto.getEmail());
        user.setPoints(0);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("User registered and saved with ID: {}", user.getId());

        String token = generateToken(user);

        return new AuthenticationResponseDto(token, user.getId(), user.getUsername(), user.getRole(), user.getPoints());
    }


    public AuthenticationResponseDto authenticateUser(UserLoginDto userLoginDto) {
        logger.info("Authenticating user with username: {}", userLoginDto.getUsername());

        // Fetch user from the database
        Optional<UserEntity> optionalUser = userRepository.findByUsername(userLoginDto.getUsername());
        if (optionalUser.isEmpty()) {
            logger.warn("Authentication failed: User not found");
            throw new ApplicationException("User not found");
        }

        UserEntity user = optionalUser.get();

        // Verify the password
        if (!passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            logger.warn("Authentication failed: Incorrect password for user {}", userLoginDto.getUsername());
            throw new ApplicationException("Incorrect password");
        }

        logger.info("User authenticated successfully with username: {}", userLoginDto.getUsername());

        String token = generateToken(user);

        return new AuthenticationResponseDto(token, user.getId(), user.getUsername(), user.getRole(), user.getPoints());
    }

    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of(
                        "userId", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "points", user.getPoints()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key)
                .compact();
    }

}
