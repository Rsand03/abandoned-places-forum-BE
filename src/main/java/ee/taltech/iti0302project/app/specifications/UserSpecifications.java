package ee.taltech.iti0302project.app.specifications;

import ee.taltech.iti0302project.app.entity.user.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    private UserSpecifications() {}

    public static Specification<UserEntity> hasMinPoints(Integer minPoints) {
        return (root, query, cb) ->
                minPoints == null ? null : cb.greaterThanOrEqualTo(root.get("points"), minPoints);
    }

    public static Specification<UserEntity> hasUsername(String username) {
        return (root, query, cb) -> {
            if (username == null || username.isEmpty()) return null;
            return cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
        };
    }

    public static Specification<UserEntity> hasRole(String role) {
        return (root, query, cb) -> {
            if (role == null || role.isEmpty()) return null;
            return cb.like(cb.lower(root.get("role")), "%" + role.toLowerCase() + "%");
        };
    }

}
