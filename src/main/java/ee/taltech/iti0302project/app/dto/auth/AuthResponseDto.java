package ee.taltech.iti0302project.app.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class AuthResponseDto {
    private String token;
    private UUID userId;
    private String username;
    private String role;
    private Integer points;
}
