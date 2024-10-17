package ee.taltech.iti0302project.app.dto;

import lombok.Data;

@Data
public class AbandonedPlaceDto {

    private Long id;
    private String name;
    private Integer location; // for testing, to be replaced with Point(lon, lat)
    private String type;
    private String condition;

}
