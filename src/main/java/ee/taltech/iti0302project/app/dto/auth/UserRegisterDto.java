package ee.taltech.iti0302project.app.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "DTO for registering a new user")
public class UserRegisterDto {

    @Schema(description = "New user's username")
    @NotNull
    @Size(min = 3, max = 30)
    @Pattern(
            regexp = "^[a-zA-Z0-9._\\-\\s]+$",
            message = "Username can only contain letters, numbers, spaces and special characters: '.', '_', and '-'."
    )
    private String username;

    @Schema(description = "New user's password")
    @NotNull
    @Size(min = 4, max = 30)
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>?/`~]+$",
            message = "Password can only contain letters, numbers and special characters: !@#$%^&*()_+-=[]{};':\"|,.<>?/`~"
    )
    private String password;

    @Schema(description = "New user's email")
    @NotNull
    @Size(max = 50)
    @Pattern(
            regexp = "[a-zA-Z]+[(a-zA-Z0-9-\\\\_.!)]*[(a-zA-Z0-9)]+@[(a-zA-Z)]+\\.[(a-zA-Z)]{2,3}",
            message = "Invalid email"
    )
    private String email;

}
