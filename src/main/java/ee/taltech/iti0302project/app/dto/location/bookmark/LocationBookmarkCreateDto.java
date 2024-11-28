package ee.taltech.iti0302project.app.dto.location.bookmark;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class LocationBookmarkCreateDto {

    @NotNull
    private BookmarkType type;

    @NotNull
    private UUID createdByUserUuid;

    @NotNull
    private UUID locationId;
}
