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

    @Schema(description = "Whether or not the filtering should ignore subCategories")
    private boolean filterByMainCategoryOnly = false;

    @Schema(description = "Ids of categories to filter by", example = "[6, 7, 8]")
    @Size(max = 15)
    private List<Long> categoryIds = new ArrayList<>();

    @Schema(description = "Id of the location condition(s) to filter by", example = "[3]")
    @Size(max = 15)
    private List<Long> conditionIds = new ArrayList<>();

    @Schema(description = "Id of the location status(es) to filter by", example = "[3]")
    @Size(max = 15)
    private List<Long> statusIds = new ArrayList<>();

    @Schema(description = "Bookmark type names to filter by", example = "JUBA_KULASTATUD")
    @Size(max = 50)
    private List<String> bookmarkTypes = new ArrayList<>();
}
