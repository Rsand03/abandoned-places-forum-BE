package ee.taltech.iti0302project.app.dto.feed;

import lombok.Data;

import java.util.UUID;

@Data
public class UpvoteDto {
    private Integer id;
    private Integer postId;
    private UUID userId;
}
