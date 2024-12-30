package ee.taltech.iti0302project.app.dto.location.attributes;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LocationConditionDto {

    private Long id;
    private String name;

}
