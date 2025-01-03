package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.auth.AuthResponseDto;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto) {

        AuthResponseDto response = authService.registerUser(userRegisterDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody UserLoginDto userLoginDto) {
            AuthResponseDto response = authService.authenticateUser(userLoginDto);
            return ResponseEntity.ok(response);
    }
}
