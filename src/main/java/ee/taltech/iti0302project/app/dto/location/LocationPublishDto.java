package ee.taltech.iti0302project.app.dto.location;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class LocationPublishDto {
    private UUID locationId;

    @Min(value = 0)
    @Max(value = 500)
    private Integer minRequiredPointsToView;
}
