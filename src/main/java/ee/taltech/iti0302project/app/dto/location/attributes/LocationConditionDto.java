package ee.taltech.iti0302project.app.dto.location.attributes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "DTO for retrieving locationCondition details")
public class LocationConditionDto {

    @Schema(description = "Location condition id", example = "1")
    private Long id;

    @Schema(description = "Location condition name", example = "Eluohtlik")
    private String name;

}
