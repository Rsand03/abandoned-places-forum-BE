package ee.taltech.iti0302project.app.dto.profile;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String currentPassword;
    private String newPassword;

}

