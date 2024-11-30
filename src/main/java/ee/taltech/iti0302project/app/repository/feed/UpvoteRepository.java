package ee.taltech.iti0302project.app.repository.feed;

import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpvoteRepository extends JpaRepository<UpvoteEntity, Long> {
    boolean existsByPostIdAndUserId(Long postId, UUID userId);

    List<UpvoteEntity> findByPostId(Long postId);

    Optional<UpvoteEntity> findByPostIdAndUserId(Long postId, UUID userId);
}
