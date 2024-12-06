package ee.taltech.iti0302project.app.dto.location.attributes;

import lombok.Data;

import java.util.List;

@Data
public class LocationAttributesDto {

    private List<LocationCategoryDto> categories;
    private List<LocationConditionDto> conditions;
    private List<LocationStatusDto> statuses;

}
