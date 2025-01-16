package ee.taltech.iti0302project.app.dto.location.bookmark;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@Schema(description = "DTO for creating a location bookmark")
public class LocationBookmarkCreateDto {

    @NotNull
    @Schema(description = "Type of location bookmark", example = "JAA_MEELDE")
    private BookmarkType type;

    @NotNull
    @Schema(description = "UUID of the user creating the bookmark", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID createdByUserUuid;

    @NotNull
    @Schema(description = "UUID of the location associated with the bookmark",
            example = "e84e2e8e-8d6f-4b8b-89d3-d4fecd81fbb3")
    private UUID locationId;
}
