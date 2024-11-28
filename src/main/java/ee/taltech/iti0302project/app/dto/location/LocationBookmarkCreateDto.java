package ee.taltech.iti0302project.app.dto.location;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class LocationBookmarkCreateDto {

    @NotNull
    @Size(max = 20)
    private String type;

    @NotNull
    private UUID createdByUserUuid;

    @NotNull
    private UUID locationId;
}
