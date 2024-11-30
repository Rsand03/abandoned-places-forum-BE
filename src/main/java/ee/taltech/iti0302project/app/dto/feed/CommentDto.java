package ee.taltech.iti0302project.app.dto.feed;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentDto {

    private Long id;
    private String body;
    private Long postId;
    private UUID createdById;
    private String createdByUsername;
    private LocalDateTime createdAt;
}
