package ee.taltech.iti0302project.app.dto.location.attributes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@Schema(description = "DTO for retrieving locationStatus details")
public class LocationStatusDto {

    @Schema(description = "Location status id", example = "1")
    private Long id;
    @Schema(description = "Location status name", example = "Raskesti ligipääsetav")
    private String name;

}
