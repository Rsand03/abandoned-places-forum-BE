package ee.taltech.iti0302project.app.repository.location;

import ee.taltech.iti0302project.app.entity.location.LocationBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationBookmarkRepository extends JpaRepository<LocationBookmarkEntity, UUID> {
    boolean existsByLocationIdAndCreatedBy(UUID locationId, UUID createdBy);

    void deleteByLocationIdAndCreatedBy(UUID locationId, UUID createdBy);
}
