package ee.taltech.iti0302project.app.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserDto {

    private UUID id;
    private String username;
    private Integer points;
    private String role;
    private LocalDateTime createdAt;

}
