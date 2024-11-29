package ee.taltech.iti0302project.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UserCriteria {

    @Min(value = 0)
    @Max(value = 5000)
    private Integer minPoints;

}
