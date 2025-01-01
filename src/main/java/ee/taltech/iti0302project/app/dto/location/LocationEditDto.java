package ee.taltech.iti0302project.app.dto.location;

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
public class LocationEditDto {

    private UUID editingUserId;

    @NotNull
    private UUID locationId;

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Min(value = 1)
    @Max(value = 5000)
    private Long mainCategoryId;

    @NotNull
    @Size(max = 5)
    @UniqueElements
    private List<Long> subCategoryIds;

    @NotNull
    @Min(value = 1)
    @Max(value = 50)
    private Long conditionId;

    @NotNull
    @Min(value = 1)
    @Max(value = 50)
    private Long statusId;

    @Size(max = 255)
    private String additionalInformation;

}
