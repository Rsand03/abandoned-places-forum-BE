package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.UserLoginDto;
import ee.taltech.iti0302project.app.dto.UserRegisterDto;
import ee.taltech.iti0302project.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterDto> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        return ResponseEntity.ok(authService.registerUser(userRegisterDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserRegisterDto> login(@RequestBody UserLoginDto userLoginDto) {
        UserRegisterDto authenticatedUser = authService.authenticateUser(userLoginDto.getUsername(), userLoginDto.getPassword());
        return ResponseEntity.ok(authenticatedUser);
    }
}
