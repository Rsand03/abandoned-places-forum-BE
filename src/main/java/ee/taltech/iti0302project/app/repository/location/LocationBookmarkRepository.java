package ee.taltech.iti0302project.app.repository.location;

import ee.taltech.iti0302project.app.entity.location.LocationBookmarkEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationBookmarkRepository extends JpaRepository<LocationBookmarkEntity, UUID> {
    boolean existsByCreatedByAndLocationAndType(UUID createdBy, LocationEntity location, String bookmarkType);

    void deleteByCreatedByAndLocationAndType(UUID createdBy, LocationEntity location, String bookmarkType);

    List<LocationBookmarkEntity> findByCreatedBy(UUID createdBy);

    List<LocationBookmarkEntity> findByCreatedByAndLocation_Id(UUID createdBy, UUID locationId);

    void deleteAllByLocationAndCreatedBy(LocationEntity location, UUID createdBy);
}
