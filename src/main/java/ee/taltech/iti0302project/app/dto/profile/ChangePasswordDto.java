package ee.taltech.iti0302project.app.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO for changing user's password")
public class ChangePasswordDto {

    @NotNull
    @Size(min = 4, max = 30)
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>?/`~]+$",
            message = "Password can only contain letters, numbers and special characters: !@#$%^&*()_+-=[]{};':\"|,.<>?/`~"
    )
    private String currentPassword;

    @NotNull
    @Size(min = 4, max = 30)
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>?/`~]+$",
            message = "Password can only contain letters, numbers and special characters: !@#$%^&*()_+-=[]{};':\"|,.<>?/`~"
    )
    private String newPassword;

}

