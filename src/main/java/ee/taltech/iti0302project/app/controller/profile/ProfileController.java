package ee.taltech.iti0302project.app.controller.profile;

import ee.taltech.iti0302project.app.dto.profile.ChangeEmailDto;
import ee.taltech.iti0302project.app.dto.profile.ChangePasswordDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService userService;
    private final JwtService jwtService;
    private final SecretKey key;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable UUID userId) {
        UserProfileDto userProfileDTO = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfileDTO);
    }

    @PutMapping("/{userId}/updateEmail")
    public ResponseEntity<UserProfileDto> updateEmail(
            @PathVariable UUID userId,
            @RequestBody ChangeEmailDto changeEmailDto,
            @RequestHeader("Authorization") String authHeader) {

        jwtService.validateUserIdAgainstJwtUserId(authHeader, userId);

        UserProfileDto updatedUser = userService.updateEmail(userId, changeEmailDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{userId}/updatePassword")
    public ResponseEntity<UserProfileDto> updatePassword(
            @PathVariable UUID userId,
            @RequestBody ChangePasswordDto changePasswordDto,
            @RequestHeader("Authorization") String authHeader) {

        jwtService.validateUserIdAgainstJwtUserId(authHeader, userId);

        UserProfileDto updatedUser = userService.updatePassword(userId, changePasswordDto);
        return ResponseEntity.ok(updatedUser);
    }

}
