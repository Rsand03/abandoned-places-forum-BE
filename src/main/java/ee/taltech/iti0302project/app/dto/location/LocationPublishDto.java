package ee.taltech.iti0302project.app.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@Schema(description = "DTO to make an existing private location public")
public class LocationPublishDto {

    @Schema(description = "UUID of the user publishing the location, extracted from auth header JWT")
    private UUID publisherId;

    @Schema(description = "UUID of the location to be published")
    @NotNull
    private UUID locationId;

    @Schema(description = "Minimum points other users need to view this public location",
            example = "20")
    @NotNull
    @Min(value = 0)
    @Max(value = 500)
    private Integer minRequiredPointsToView;
}
