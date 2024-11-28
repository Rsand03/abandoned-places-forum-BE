package ee.taltech.iti0302project.app.service.profile;

import ee.taltech.iti0302project.app.dto.mapper.user.UserMapper;
import ee.taltech.iti0302project.app.dto.profile.ChangeEmailDto;
import ee.taltech.iti0302project.app.dto.profile.ChangeEmailPasswordDto;
import ee.taltech.iti0302project.app.dto.profile.ChangePasswordDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserProfileDto getUserProfile(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserProfileDto(user);
    }

    public UserProfileDto updateEmail(UUID userId, ChangeEmailDto changeEmailDto) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(changeEmailDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        user.setEmail(changeEmailDto.getNewEmail());
        userRepository.save(user);

        return userMapper.toUserProfileDto(user);
    }

    public UserProfileDto updatePassword(UUID userId, ChangePasswordDto changePasswordDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserProfileDto updateEmailAndPassword(UUID userId, ChangeEmailPasswordDto changeEmailPasswordDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields
        user.setEmail(changeEmailPasswordDto.getEmail());
        user.setPassword(changeEmailPasswordDto.getPassword());  // Ideally, password should be encrypted

        UserEntity updatedUser = userRepository.save(user);
        return userMapper.toUserProfileDto(updatedUser);
    }
}

