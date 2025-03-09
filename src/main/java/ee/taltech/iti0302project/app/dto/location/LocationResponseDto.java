package ee.taltech.iti0302project.app.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationCategoryDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationConditionDto;
import ee.taltech.iti0302project.app.dto.location.attributes.LocationStatusDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "DTO for retrieving a location")
public class LocationResponseDto {

    @Schema(description = "UUID of the location")
    private UUID id;

    @Schema(description = "Name of the location", example = "Abandoned residential building")
    private String name;

    @Schema(example = "26.751234")
    private double lon;

    @Schema(example = "58.105678")
    private double lat;

    @Schema(description = "LocationCategory object containing id, name and colorHex")
    private LocationCategoryDto mainCategory;

    @Schema(description = "List of locationCategory objects containing id, name and colorHex")
    private List<LocationCategoryDto> subCategories;

    @Schema(description = "LocationCondition object containing id, name")
    private LocationConditionDto condition;

    @Schema(description = "LocationStatus object containing id, name")
    private LocationStatusDto status;

    @Schema(description = "Additional info about the location",
            example = "Interior is generally well-preserved, but the roof is leaking and the ceilings might collapse")
    private String additionalInformation;

    @Schema(description = "Whether the location is private or public")
    @JsonProperty("isPublic")
    private boolean isPublic;

    @Schema(description = "Whether this location is currently in the process of becoming public (currently not used)")
    @JsonProperty("isPendingPublicationApproval")
    private boolean isPendingPublicationApproval;

    @Schema(description = "Minimum points required for users to view this location, used only for public locations",
            example = "20")
    private int minRequiredPointsToView;

}
