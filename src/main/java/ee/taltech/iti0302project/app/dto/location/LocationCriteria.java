package ee.taltech.iti0302project.app.dto.location;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LocationCriteria {

    private UUID userId;

    @Min(value = 1)
    @Max(value = 5000)
    private Long mainCategoryId;

    @Size(max = 5)
    private List<Long> subCategoryIds;

    @Min(value = 1)
    @Max(value = 50)
    private Long conditionId;

    @Min(value = 1)
    @Max(value = 50)
    private Long statusId;

    @Min(value = 0)
    @Max(value = 500)
    private Integer minRequiredPointsToView;

}
