package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.UserDto;
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
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(authService.registerUser(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
        UserDto authenticatedUser = authService.authenticateUser(userDto.getUsername(), userDto.getPassword());
        return ResponseEntity.ok(authenticatedUser);
    }
}
