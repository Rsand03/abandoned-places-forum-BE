package ee.taltech.iti0302project.app.dto.location;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@Schema(description = "DTO for editing an existing private location")
public class LocationEditDto {

    @Schema(description = "UUID of the user editing the location, extracted from auth header JWT")
    private UUID editingUserId;

    @Schema(description = "UUID of the edited location", example = "d43d3a9b-f1c3-467e-b70d-b9806d8507f2")
    @NotNull
    private UUID id;

    @Schema(example = "Abandoned residential building")
    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @Schema(description = "Id to match locationCategory", example = "5")
    @NotNull
    @Min(value = 1)
    @Max(value = 5000)
    private Long mainCategoryId;

    @Schema(description = "Ids to match locationCategories", example = "[1, 2, 8, 9]")
    @NotNull
    @Size(max = 5)
    @UniqueElements
    private List<Long> subCategoryIds;

    @Schema(description = "Id to match locationCondition", example = "2")
    @NotNull
    @Min(value = 1)
    @Max(value = 50)
    private Long conditionId;

    @Schema(description = "Id to match locationStatus", example = "2")
    @NotNull
    @Min(value = 1)
    @Max(value = 50)
    private Long statusId;

    @Schema(example = "Interior is generally well-preserved, but the roof is leaking and the ceilings might collapse")
    @Size(max = 255)
    private String additionalInformation;

}
