package ee.taltech.iti0302project.app.dto.auth;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String username;
    private String email;
    private String password;
}
