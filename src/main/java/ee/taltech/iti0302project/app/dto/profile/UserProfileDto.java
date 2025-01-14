package ee.taltech.iti0302project.app.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO for user profile")
public class UserProfileDto {

    @NotNull
    @Size(min = 3, max = 30)
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @NotNull
    @Email
    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @NotNull
    @Schema(description = "Points of the user", example = "20")
    private Integer points;

    @NotNull
    @Schema(description = "Role of the user", example = "ADMIN")
    private String role;
}
