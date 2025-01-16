package ee.taltech.iti0302project.app.repository.location;

import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<LocationEntity, UUID>,
        JpaSpecificationExecutor<LocationEntity> {

    long countByIsPublicFalseAndCreatedBy(UUID createdBy);

    long countByIsPublicTrue();

    List<LocationEntity> findAllByIsPublicTrue();

}
