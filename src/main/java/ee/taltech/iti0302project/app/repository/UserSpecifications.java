package ee.taltech.iti0302project.app.repository;

import ee.taltech.iti0302project.app.entity.user.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    private UserSpecifications() {}

    public static Specification<UserEntity> hasMorePointsThan(Integer min) {
        return (root, query, cb) ->
                min == null ? null : cb.greaterThanOrEqualTo(root.get("points"), min);
    }

}
