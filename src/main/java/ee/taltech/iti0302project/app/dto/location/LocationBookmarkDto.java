package ee.taltech.iti0302project.app.dto.location;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LocationBookmarkDto {
    private UUID id;
    private String type;
    private String locationName;
    private LocalDateTime createdAt;
}
