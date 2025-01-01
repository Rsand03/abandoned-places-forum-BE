package ee.taltech.iti0302project.app.repository.location;

import ee.taltech.iti0302project.app.entity.location.LocationBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationBookmarkRepository extends JpaRepository<LocationBookmarkEntity, UUID> {
    boolean existsByCreatedByAndLocationIdAndType(UUID createdBy, UUID locationId, String bookmarkType);

    void deleteByCreatedByAndLocationIdAndType(UUID createdBy, UUID locationId, String bookmarkType);

    List<LocationBookmarkEntity> findByCreatedBy(UUID createdBy);

    List<LocationBookmarkEntity> findByCreatedByAndLocationId(UUID createdBy, UUID locationId);

    void deleteAllByLocationIdAndCreatedBy(UUID locationId, UUID createdBy);
}
