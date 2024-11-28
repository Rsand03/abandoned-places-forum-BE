package ee.taltech.iti0302project.app.controller.profile;

import ee.taltech.iti0302project.app.dto.profile.ChangeEmailDto;
import ee.taltech.iti0302project.app.dto.profile.ChangeEmailPasswordDto;
import ee.taltech.iti0302project.app.dto.profile.ChangePasswordDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.service.profile.ProfileService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService userService;
    private final SecretKey key;

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

    @PutMapping("/{userId}/updateEmail")
    public ResponseEntity<UserProfileDto> updateEmail(
            @PathVariable UUID userId,
            @RequestBody ChangeEmailDto changeEmailDto,
            @RequestHeader("Authorization") String authHeader) {

        validateUserIdFromToken(authHeader, userId);

        UserProfileDto updatedUser = userService.updateEmail(userId, changeEmailDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{userId}/updatePassword")
    public ResponseEntity<UserProfileDto> updatePassword(
            @PathVariable UUID userId,
            @RequestBody ChangePasswordDto changePasswordDto,
            @RequestHeader("Authorization") String authHeader) {

        validateUserIdFromToken(authHeader, userId);

        UserProfileDto updatedUser = userService.updatePassword(userId, changePasswordDto);
        return ResponseEntity.ok(updatedUser);
    }

    private void validateUserIdFromToken(String authHeader, UUID userId) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenUserId = claims.get("userId", String.class);

            if (!userId.toString().equals(tokenUserId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to modify this user");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

}
