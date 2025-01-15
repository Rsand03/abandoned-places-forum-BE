package ee.taltech.iti0302project.app.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Data Transfer Object representing a user profile")
public class UserDto {

    @Schema(description = "The username of the user, up to 100 characters", example = "john_doe", maxLength = 100)
    @Size(max = 100)
    private String username;

    @Schema(description = "The minimum points of the user, ranging from 0 to 5000", example = "1500", minimum = "0", maximum = "5000")
    @Min(value = 0)
    @Max(value = 5000)
    private Integer minPoints;

    @Schema(description = "The role of the user, up to 20 characters", example = "admin", maxLength = 20)
    @Size(max = 20)
    private String role;
}
