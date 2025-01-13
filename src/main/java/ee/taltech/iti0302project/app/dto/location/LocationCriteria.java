package ee.taltech.iti0302project.app.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Criteria for filter locations")
public class LocationCriteria {

    @Schema(description = "UUID of the user applying the filter, extracted from auth header JWT")
    private UUID userId;

    @Schema(description = "Minimum points required to view public locations created by others (default query filter)",
            example = "50")
    @Min(value = 0)
    @Max(value = 500)
    private Long userPoints;

    @Schema(description = "Id of the main location category to filter by", example = "5")
    @Min(value = 1)
    @Max(value = 5000)
    private Long mainCategoryId;

    @Schema(description = "Ids of subcategories to filter by", example = "[6, 7, 8]")
    @Size(max = 5)
    private List<Long> subCategoryIds = new ArrayList<>();

    @Schema(description = "Id of the location condition to filter by", example = "3")
    @Min(value = 1)
    @Max(value = 50)
    private Long conditionId;

    @Schema(description = "Id of the location status to filter by", example = "3")
    @Min(value = 1)
    @Max(value = 50)
    private Long statusId;

}
