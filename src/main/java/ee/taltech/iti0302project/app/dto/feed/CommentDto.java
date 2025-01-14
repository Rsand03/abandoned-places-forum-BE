package ee.taltech.iti0302project.app.dto.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "DTO representing a comment")
public class CommentDto {

    @Schema(description = "Unique identifier of the comment", example = "1")
    private Long id;

    @NotNull
    @Size(min = 1, max = 500, message = "Comment body must be between 1 and 500 characters")
    @Schema(description = "Content of the comment", example = "This is a sample comment.")
    private String body;

    @NotNull
    @Schema(description = "Identifier of the post this comment belongs to", example = "1001")
    private Long postId;

    @NotNull
    @Schema(description = "UUID of the user who created the comment", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID createdById;

    @NotNull
    @Size(min = 1, max = 50, message = "Username must be between 1 and 50 characters")
    @Schema(description = "Username of the user who created the comment", example = "johndoe123")
    private String createdByUsername;

    @Schema(description = "Timestamp when the comment was created", example = "2025-01-14T15:32:00")
    private LocalDateTime createdAt;
}
