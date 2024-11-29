package ee.taltech.iti0302project.app.dto.feed;

import lombok.Data;

import java.util.UUID;

@Data
public class CreatePostDto {

    private UUID userId;
    private String title;
    private String body;
}