package ee.taltech.iti0302project.app.repository.location;

import ee.taltech.iti0302project.app.entity.location.LocationBookmarkEntity;
import ee.taltech.iti0302project.app.entity.location.LocationCategoryEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class LocationSpecifications {

    private LocationSpecifications() {
    }

    public static Specification<LocationEntity> hasCreatedByOrIsPublicAndUserHasEnoughPoints(UUID userId, Long minPoints) {
        return (root, query, cb) ->
                cb.or(
                        cb.equal(root.get("createdBy"), userId),
                        cb.and(
                                cb.isTrue(root.get("isPublic")),
                                cb.lessThanOrEqualTo(root.get("minRequiredPointsToView"), minPoints)
                        )
                );
    }

    public static Specification<LocationEntity> hasMainCategory(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return null;
            }
            return root.get("mainCategory").get("id").in(categoryIds);
        };
    }

    public static Specification<LocationEntity> hasMainCategoryOrSubcategory(List<Long> categoryIds) {
        return (root, query, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return null;
            }
            Join<LocationEntity, LocationCategoryEntity> subCategoryJoin = root.join("subCategories", JoinType.LEFT);

            Predicate mainCategoryPredicate = cb.isTrue(
                    root.get("mainCategory").get("id").in(categoryIds)
            );
            Predicate subCategoryPredicate = cb.isTrue(
                    subCategoryJoin.get("id").in(categoryIds)
            );

            return cb.or(mainCategoryPredicate, subCategoryPredicate);
        };
    }

    public static Specification<LocationEntity> hasCondition(List<Long> conditionIds) {
        return (root, query, cb) -> {
            if (conditionIds == null || conditionIds.isEmpty()) {
                return null;
            }
            return root.get("condition").get("id").in(conditionIds);
        };
    }

    public static Specification<LocationEntity> hasStatus(List<Long> statusIds) {
        return (root, query, cb) -> {
            if (statusIds == null || statusIds.isEmpty()) {
                return null;
            }
            return root.get("status").get("id").in(statusIds);
        };
    }

    public static Specification<LocationEntity> hasBookmarkTypes(List<String> bookmarkTypes) {
        return (root, query, cb) -> {
            if (bookmarkTypes == null || bookmarkTypes.isEmpty()) {
                return null;
            }

            Join<LocationEntity, LocationBookmarkEntity> bookmarksJoin = root.join("bookmarks", JoinType.INNER);

            return cb.and(bookmarksJoin.get("type").in(bookmarkTypes));
        };
    }
}
