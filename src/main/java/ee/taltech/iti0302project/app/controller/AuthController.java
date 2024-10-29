package ee.taltech.iti0302project.app.controller;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.UserRegisterDto;
import ee.taltech.iti0302project.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
        String JWToken = authService.registerUser(userRegisterDto);

        // Create a JSON response containing the token
        Map<String, String> response = new HashMap<>();
        response.put("token", JWToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginDto userLoginDto) {
        try {
            String JWToken = authService.authenticateUser(userLoginDto.getUsername(), userLoginDto.getPassword());

            // Create a JSON response containing the token
            Map<String, String> response = new HashMap<>();
            response.put("token", JWToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return a JSON error response if authentication fails
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
