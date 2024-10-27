package ee.taltech.iti0302project.app.repository;

import ee.taltech.iti0302project.app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
