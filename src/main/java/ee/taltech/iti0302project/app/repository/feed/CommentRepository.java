package ee.taltech.iti0302project.app.repository.feed;

import ee.taltech.iti0302project.app.entity.feed.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByPostId(Integer postId);
}
