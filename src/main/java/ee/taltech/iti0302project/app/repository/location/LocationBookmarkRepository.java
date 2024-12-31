package ee.taltech.iti0302project.app.repository.location;

import ee.taltech.iti0302project.app.entity.location.LocationBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationBookmarkRepository extends JpaRepository<LocationBookmarkEntity, UUID> {
    boolean existsByIdAndCreatedBy(UUID id, UUID createdBy);

    void deleteByIdAndCreatedBy(UUID id, UUID createdBy);

    List<LocationBookmarkEntity> findByCreatedBy(UUID createdBy);

    List<LocationBookmarkEntity> findByCreatedByAndLocationId(UUID createdBy, UUID locationId);
}
