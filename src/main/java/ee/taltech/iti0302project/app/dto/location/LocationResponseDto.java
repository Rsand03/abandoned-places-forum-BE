package ee.taltech.iti0302project.app.dto.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import ee.taltech.iti0302project.app.entity.location.LocationCategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LocationResponseDto {

    private UUID id;
    private String name;
    private double lon;
    private double lat;

    private LocationCategoryEntity mainCategory;
    private List<LocationCategoryEntity> subCategories;
    private String condition;
    private String status;

    private String additionalInformation;
    @JsonProperty("isPublic")
    private boolean isPublic;
    @JsonProperty("isPendingPublicationApproval")
    private boolean isPendingPublicationApproval;
    private int minRequiredPointsToView;

}
