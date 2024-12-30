package ee.taltech.iti0302project.app.dto.location.attributes;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LocationCategoryDto {

    private Long id;
    private String name;
    private String colorHex;

}
