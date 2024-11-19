package ee.taltech.iti0302project.app.repository.feed;

import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {



}
