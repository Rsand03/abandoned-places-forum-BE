package ee.taltech.iti0302project.app.dto.location.attributes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "DTO containing categorized attributes of a location: categories, conditions, and statuses.")
public class LocationAttributesDto {

    private List<LocationCategoryDto> categories;
    private List<LocationConditionDto> conditions;
    private List<LocationStatusDto> statuses;

}
