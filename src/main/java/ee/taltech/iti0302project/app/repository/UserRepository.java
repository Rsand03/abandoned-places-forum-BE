package ee.taltech.iti0302project.app.repository;

import ee.taltech.iti0302project.app.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsById(UUID uuid);

    Optional<UserEntity> findById(UUID id);

    @Query("SELECT u.points FROM UserEntity u WHERE u.id = :userId")
    Long findUserPointsById(@Param("userId") UUID userId);
}
