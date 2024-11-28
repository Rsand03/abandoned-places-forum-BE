package ee.taltech.iti0302project.app.dto.profile;

import lombok.Data;

@Data
public class ChangeEmailDto {
    private String newEmail;
    private String password;
}

