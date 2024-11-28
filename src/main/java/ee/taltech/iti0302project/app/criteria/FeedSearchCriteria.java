package ee.taltech.iti0302project.app.criteria;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record FeedSearchCriteria(
        @Size(max = 50)
        String title,
        @Size(max = 100)
        String body,
        UUID locationId,
        @Size(max = 100)
        String createdByUsername,
        @Past
        LocalDate createdDateFrom,
        @PastOrPresent
        LocalDate createdDateTo,
        @Size(max = 100)
        String sortBy,
        @Size(max = 100)
        String sortDirection,
        @PositiveOrZero
        Integer page,
        @Positive
        Integer pageSize

) {
}
