package ee.taltech.iti0302project.app.service;

import ee.taltech.iti0302project.app.dto.UserDto;
import ee.taltech.iti0302project.app.dto.mapper.UserMapper;
import ee.taltech.iti0302project.app.entity.UserEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto registerUser(UserDto userDto) {
        logger.info("Registering user with username: {}", userDto.getUsername());

        UserEntity user = new UserEntity();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setPoints(0);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
        logger.info("User registered and saved with ID: {}", user.getId());

        return userMapper.toDto(user);
    }
}
