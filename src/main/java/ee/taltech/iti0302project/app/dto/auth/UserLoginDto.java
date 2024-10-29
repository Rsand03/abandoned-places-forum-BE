package ee.taltech.iti0302project.app.dto.auth;

import lombok.Data;

@Data
public class UserLoginDto {
    private String username;
    private String password;
}
