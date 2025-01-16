package ee.taltech.iti0302project.app.dto.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "DTO for upvoting a post")
public class CreateUpvoteDto {

    @NotNull
    @Schema(description = "Unique identifier of the post being upvoted", example = "100")
    private Long postId;

    @NotNull
    @Schema(description = "UUID of the user performing the upvote", example = "e84e2e8e-8d6f-4b8b-89d3-d4fecd81fbb3")
    private UUID userId;
}
