package ee.taltech.iti0302project.app.pagination;

import java.util.List;

public record PageResponse<T> (
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
