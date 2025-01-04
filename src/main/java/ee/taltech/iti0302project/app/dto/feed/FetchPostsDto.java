package ee.taltech.iti0302project.app.dto.feed;

import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Data
public class FetchPostsDto {

    private Long id;
    private String title;
    private String body;
    private UUID locationId;
    private String createdByUsername;
    private LocalDate createdAt;
    private Long likeCount;
    private Long commentCount;
    private boolean hasUpvoted;
    private LocationResponseDto location;
}
