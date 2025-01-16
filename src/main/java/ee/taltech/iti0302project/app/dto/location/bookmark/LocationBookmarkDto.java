package ee.taltech.iti0302project.app.dto.location.bookmark;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "DTO for a location bookmark")
public class LocationBookmarkDto {

    @NotNull
    @Schema(description = "Type of location bookmark", example = "JAA_MEELDE")
    private String type;
}
