package ee.taltech.iti0302project.app.criteria;

import jakarta.validation.constraints.*;

public record UserCriteria(
        @Min(value = 0)
        @Max(value = 5000)
        Integer minPoints,
        @Size(max = 100)
        String username,
        @Size(max = 20)
        String role,
        @Size(max = 100)
        String sortBy,
        @Size(max = 100)
        String sortDirection,
        @PositiveOrZero
        Integer page,
        @Positive
        Integer pageSize) {
}
