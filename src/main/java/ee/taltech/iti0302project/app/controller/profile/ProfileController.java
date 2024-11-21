package ee.taltech.iti0302project.app.controller.profile;

import ee.taltech.iti0302project.app.dto.profile.ChangeEmailPasswordDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.service.profile.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable UUID userId) {
        UserProfileDto userProfileDTO = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfileDTO);
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<UserProfileDto> updateEmailAndPassword(
            @PathVariable UUID userId,
            @RequestBody ChangeEmailPasswordDto changeEmailPasswordDTO) {
        UserProfileDto updatedUser = userService.updateEmailAndPassword(userId, changeEmailPasswordDTO);
        return ResponseEntity.ok(updatedUser);
    }
}
