package ee.taltech.iti0302project.app.dto.auth;

import lombok.Data;

import java.util.UUID;

@Data
public class UserLoginDto {
    private UUID userID;
    private String username;
    private String password;
}
