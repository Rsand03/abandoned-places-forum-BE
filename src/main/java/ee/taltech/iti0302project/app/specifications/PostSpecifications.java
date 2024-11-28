package ee.taltech.iti0302project.app.specifications;

import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PostSpecifications {
    public static Specification<PostEntity> postedBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from == null) return cb.lessThanOrEqualTo(root.get("createdAt"), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            return cb.between(root.get("createdAt"), from, to);
        };
    }
}
