package ee.taltech.iti0302project.app.dto;

import lombok.Data;

@Data
public class UserRegisterDto {
    private String username;
    private String email;
    private String password;
}
