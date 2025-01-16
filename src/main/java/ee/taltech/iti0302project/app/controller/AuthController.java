package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.auth.AuthResponseDto;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "200", description = "User registered and jwt retrieved")
    @ApiResponse(responseCode = "400", description = "Invalid userRegisterDto")
    @PostMapping("/public/auth/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto) {

        AuthResponseDto response = authService.registerUser(userRegisterDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Log in by retrieving jwt")
    @ApiResponse(responseCode = "200", description = "Jwt retrieved")
    @ApiResponse(responseCode = "400", description = "Invalid userLoginDto")
    @PostMapping("/public/auth/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        AuthResponseDto response = authService.authenticateUser(userLoginDto);
        return ResponseEntity.ok(response);
    }
}
