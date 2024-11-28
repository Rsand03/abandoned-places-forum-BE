package ee.taltech.iti0302project.app.specifications;

import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PostSpecifications {

    private PostSpecifications() {}

    public static final String CREATED_AT_COLUMN = "createdAt";

    public static Specification<PostEntity> postedBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from == null) return cb.lessThanOrEqualTo(root.get(CREATED_AT_COLUMN), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get(CREATED_AT_COLUMN), from);
            return cb.between(root.get(CREATED_AT_COLUMN), from, to);
        };
    }

    public static Specification<PostEntity> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isEmpty()) return null;
            return cb.like(root.get("title"), "%" + title + "%");
        };
    }

    public static Specification<PostEntity> hasBody(String body) {
        return (root, query, cb) -> {
            if (body == null || body.isEmpty()) return null;
            return cb.like(root.get("body"), "%" + body + "%");
        };
    }

    public static Specification<PostEntity> createdByUsername(String username) {
        return (root, query, cb) -> {
            if (username == null || username.isEmpty()) return null;
            return cb.equal(root.join("createdBy").get("username"), username);
        };
    }
}
