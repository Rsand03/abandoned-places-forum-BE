package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.auth.AuthenticationResponseDto;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://tiim32.zapto.org"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> registerUser(@RequestBody UserRegisterDto userRegisterDto) {

        AuthenticationResponseDto response = authService.registerUser(userRegisterDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody UserLoginDto userLoginDto) {
            AuthenticationResponseDto response = authService.authenticateUser(userLoginDto);
            return ResponseEntity.ok(response);
    }
}
