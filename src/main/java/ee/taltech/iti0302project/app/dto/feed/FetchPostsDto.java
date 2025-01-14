package ee.taltech.iti0302project.app.dto.feed;

import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@Schema(description = "DTO for fetching posts")
public class FetchPostsDto {

    @NotNull
    @Schema(description = "Unique identifier of the post", example = "1")
    private Long id;

    @NotNull
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    @Schema(description = "Title of the post", example = "Amazing Nature")
    private String title;

    @NotNull
    @Size(min = 1, max = 5000, message = "Body must be between 1 and 5000 characters")
    @Schema(description = "Content of the post", example = "This is a post about the beauty of nature.")
    private String body;

    @NotNull
    @Schema(description = "UUID of the location associated with the post", example = "e84e2e8e-8d6f-4b8b-89d3-d4fecd81fbb3")
    private UUID locationId;

    @NotNull
    @Size(min = 1, max = 50, message = "Username must be between 1 and 50 characters")
    @Schema(description = "Username of the post creator", example = "JohnDoe123")
    private String createdByUsername;

    @NotNull
    @Schema(description = "Date when the post was created", example = "2025-01-01")
    private LocalDate createdAt;

    @Schema(description = "Number of likes on the post", example = "42")
    private Long likeCount;

    @Schema(description = "Number of comments on the post", example = "10")
    private Long commentCount;

    @Schema(description = "Indicates if the current user has upvoted the post", example = "true")
    private boolean hasUpvoted;

    @Schema(description = "Details about the location associated with the post")
    private LocationResponseDto location;
}
