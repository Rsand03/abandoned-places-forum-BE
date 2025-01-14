package ee.taltech.iti0302project.app.dto.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "DTO for creating a new post")
public class CreatePostDto {

    @NotNull
    @Schema(description = "UUID of the user creating the post", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID userId;

    @NotNull
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    @Schema(description = "Title of the post", example = "My First Post")
    private String title;

    @NotNull
    @Size(min = 1, max = 5000, message = "Body must be between 1 and 5000 characters")
    @Schema(description = "Content of the post", example = "This is the body of my first post.")
    private String body;

    @NotNull
    @Schema(description = "UUID of the location associated with the post", example = "e84e2e8e-8d6f-4b8b-89d3-d4fecd81fbb3")
    private UUID locationId;
}