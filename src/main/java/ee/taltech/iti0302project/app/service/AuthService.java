package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.UserDto;
import ee.taltech.iti0302project.app.dto.mapper.UserMapper;
import ee.taltech.iti0302project.app.entity.UserEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto registerUser(UserDto userDto) {
        logger.info("Registering user with username: {}", userDto.getUsername());

        // Check if the username already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username is already in use.");
        }

        // Check if the email already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        UserEntity user = new UserEntity();
        user.setUsername(userDto.getUsername());

        // Hash the password before setting it in the entity
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(hashedPassword);

        user.setEmail(userDto.getEmail());
        user.setPoints(0);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("User registered and saved with ID: {}", user.getId());

        return userMapper.toDto(user);
    }


    public UserDto authenticateUser(String username, String password) {
        logger.info("Authenticating user with username: {}", username);

        // Fetch user from the database
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            logger.warn("Authentication failed: User not found");
            throw new RuntimeException("User not found");
        }

        UserEntity user = optionalUser.get();

        // Verify the password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Authentication failed: Incorrect password for user {}", username);
            throw new RuntimeException("Incorrect password");
        }

        logger.info("User authenticated successfully with username: {}", username);
        return userMapper.toDto(user);
    }
}
