package ee.taltech.iti0302project.app.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for user profile")
public class UserProfileDto {

    private String username;

    private String email;

    @Schema(description = "Points of the user", example = "20")
    private Integer points;

    @Schema(description = "Role of the user", example = "ADMIN")
    private String role;
}
