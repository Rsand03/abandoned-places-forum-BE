package ee.taltech.iti0302project.app.dto.profile;

import lombok.Data;

@Data
public class UserProfileDto {
    private String username;
    private String email;
    private Integer points;
    private String role;
}
