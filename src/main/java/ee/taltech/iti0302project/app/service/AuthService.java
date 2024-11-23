package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.dto.auth.AuthenticationResponseDto;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

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

        String token = generateToken(user.getUsername());

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

        String token = generateToken(userLoginDto.getUsername());

        return new AuthenticationResponseDto(token, user.getId(), user.getUsername(), user.getRole(), user.getPoints());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

}
