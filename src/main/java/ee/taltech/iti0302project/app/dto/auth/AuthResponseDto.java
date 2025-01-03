package ee.taltech.iti0302project.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private UUID userId;
    private String username;
    private String role;
    private Integer points;
}
