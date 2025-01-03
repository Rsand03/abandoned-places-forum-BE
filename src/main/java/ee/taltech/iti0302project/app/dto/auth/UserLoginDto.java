package ee.taltech.iti0302project.app.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserLoginDto {

    @NotNull
    @Size(min = 3, max = 30)
    @Pattern(
            regexp = "^[a-zA-Z0-9._\\-\\s]+$",
            message = "Username can only contain letters, numbers, spaces and special characters: '.', '_', and '-'."
    )
    private String username;

    @NotNull
    @Size(min = 4, max = 30)
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>?/`~]+$",
            message = "Password can only contain letters, numbers and special characters: !@#$%^&*()_+-=[]{};':\"|,.<>?/`~"
    )
    private String password;
}
