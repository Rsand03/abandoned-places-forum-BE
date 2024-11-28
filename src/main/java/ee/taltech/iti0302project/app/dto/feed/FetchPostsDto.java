package ee.taltech.iti0302project.app.dto.feed;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class FetchPostsDto {

    private Integer id;
    private String title;
    private String body;
    private UUID locationId;
    private String createdByUsername;
    private LocalDate createdAt;
}
