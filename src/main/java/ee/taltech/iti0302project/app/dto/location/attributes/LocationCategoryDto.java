package ee.taltech.iti0302project.app.dto.location.attributes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "DTO for retrieving locationCategory details")
public class LocationCategoryDto {

    @Schema(description = "Location category id", example = "1")
    private Long id;

    @Schema(description = "Location category name", example = "Ladu")
    private String name;

    @Schema(description = "Color representing the category", example = "8A1616")
    private String colorHex;

}
