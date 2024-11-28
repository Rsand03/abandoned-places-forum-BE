package ee.taltech.iti0302project.app.dto.location.bookmark;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LocationBookmarkDto {
    private UUID id;
    private String type;
    private UUID locationId;
    private LocalDateTime createdAt;
}
