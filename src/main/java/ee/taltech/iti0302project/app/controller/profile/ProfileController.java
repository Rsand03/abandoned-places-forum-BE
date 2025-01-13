package ee.taltech.iti0302project.app.controller.profile;

import ee.taltech.iti0302project.app.dto.profile.ChangeEmailDto;
import ee.taltech.iti0302project.app.dto.profile.ChangePasswordDto;
import ee.taltech.iti0302project.app.dto.profile.UserProfileDto;
import ee.taltech.iti0302project.app.exception.ForbiddenException;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import ee.taltech.iti0302project.app.service.profile.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "User profile")
public class ProfileController {

    private final ProfileService userService;
    private final JwtService jwtService;

    @Operation(summary = "Retrieve user profile by userId")
    @ApiResponse(responseCode = "200", description = "User profile retrieved")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @PathVariable UUID userId,
            @RequestHeader("Authorization") String authHeader) {

        try {
            jwtService.validateUserIdAgainstJwtUserId(authHeader, userId);
        } catch(ForbiddenException errorResponse) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


        UserProfileDto userProfileDTO = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfileDTO);
    }

    @Operation(summary = "Change the email of an existing user")
    @ApiResponse(responseCode = "200", description = "User email updated")
    @PutMapping("/{userId}/updateEmail")
    public ResponseEntity<UserProfileDto> updateEmail(
            @PathVariable UUID userId,
            @RequestBody ChangeEmailDto changeEmailDto,
            @RequestHeader("Authorization") String authHeader) {

        try {
            jwtService.validateUserIdAgainstJwtUserId(authHeader, userId);
        } catch(ForbiddenException errorResponse) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserProfileDto updatedUser = userService.updateEmail(userId, changeEmailDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Change the password of an existing user")
    @ApiResponse(responseCode = "200", description = "User password updated")
    @PutMapping("/{userId}/updatePassword")
    public ResponseEntity<UserProfileDto> updatePassword(
            @PathVariable UUID userId,
            @RequestBody ChangePasswordDto changePasswordDto,
            @RequestHeader("Authorization") String authHeader) {

        try {
            jwtService.validateUserIdAgainstJwtUserId(authHeader, userId);
        } catch(ForbiddenException errorResponse) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserProfileDto updatedUser = userService.updatePassword(userId, changePasswordDto);
        return ResponseEntity.ok(updatedUser);
    }

}
