package ee.taltech.iti0302project.app.dto.profile;

import lombok.Data;

@Data
public class ChangeEmailPasswordDto {
    private String email;
    private String password;
}
