package ee.taltech.iti0302project.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@Schema(description = "DTO for retrieving registering or logging in data")
public class AuthResponseDto {

    @Schema(description = "JWT")
    private String token;

    @Schema(description = "UUID of the user")
    private UUID userId;

    @Schema(description = "Username of the user")
    private String username;

    @Schema(description = "Role of the user", example = "ADMIN")
    private String role;

    @Schema(description = "Points of the user", example = "20")
    private Integer points;

}
