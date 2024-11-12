package ee.taltech.iti0302project.app.dto.feed;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Integer id;
    private String comment;
    private Integer postId;
    private String createdByUsername;
    private LocalDateTime createdAt;
}
