package ee.taltech.iti0302project.app.repository.location;

import ee.taltech.iti0302project.app.entity.location.LocationBookmarkEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
