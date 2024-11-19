package ee.taltech.iti0302project.app.dto.auth;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthenticationResponseDto {
    private String token;
    private UUID userId;
    private String username;
    private String role;
    private Integer points;

    public AuthenticationResponseDto(String token, UUID userId, String username, String role, Integer points) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.points = points;
    }
}
