package ee.taltech.iti0302project.app.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO for changing user's email")
public class ChangeEmailDto {

    @NotNull
    @Size(max = 50)
    @Pattern(
            regexp = "[a-zA-Z]+[(a-zA-Z0-9-\\\\_.!)]*[(a-zA-Z0-9)]+@[(a-zA-Z)]+\\.[(a-zA-Z)]{2,3}",
            message = "Invalid email"
    )
    private String newEmail;

    @NotNull
    @Size(min = 4, max = 30)
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>?/`~]+$",
            message = "Password can only contain letters, numbers and special characters: !@#$%^&*()_+-=[]{};':\"|,.<>?/`~"
    )
    private String password;
}

