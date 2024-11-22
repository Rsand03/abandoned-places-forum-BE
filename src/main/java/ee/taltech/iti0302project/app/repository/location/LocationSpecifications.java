package ee.taltech.iti0302project.app.repository.location;

import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class LocationSpecifications {

    private LocationSpecifications() {}

    public static Specification<LocationEntity> hasCreatedBy(UUID userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("createdBy"), userId);
    }

    public static Specification<LocationEntity> hasMainCategory(Long mainCategoryId) {
        return (root, query, cb) ->
                mainCategoryId == null ? null : cb.equal(root.get("mainCategory").get("id"), mainCategoryId);
    }

    public static Specification<LocationEntity> hasSubcategories(List<Long> subcategoryIds) {
        return (root, query, cb) -> {
            if (subcategoryIds == null || subcategoryIds.isEmpty()) {
                return null;
            }
            return root.join("subCategories").get("id").in(subcategoryIds);
        };
    }

    public static Specification<LocationEntity> hasCondition(Long conditionId) {
        return (root, query, cb) ->
                conditionId == null ? null : cb.equal(root.get("condition").get("id"), conditionId);
    }

    public static Specification<LocationEntity> hasStatus(Long statusId) {
        return (root, query, cb) ->
                statusId == null ? null : cb.equal(root.get("status").get("id"), statusId);
    }

    public static Specification<LocationEntity> minPointsToViewHigherThan(Integer min) {
        return (root, query, cb) ->
                min == null ? null : cb.greaterThanOrEqualTo(root.get("minRequiredPointsToView"), min);
    }

}
