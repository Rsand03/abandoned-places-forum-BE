package ee.taltech.iti0302project.app.dto.location.attributes;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LocationStatusDto {

    private Long id;
    private String name;

}
