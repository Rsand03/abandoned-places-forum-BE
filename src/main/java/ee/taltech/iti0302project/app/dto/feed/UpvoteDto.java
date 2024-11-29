package ee.taltech.iti0302project.app.dto.feed;

import lombok.Data;

import java.util.UUID;

@Data
public class UpvoteDto {
    private Long id;
    private Long postId;
    private UUID userId;
}
