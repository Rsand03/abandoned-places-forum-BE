package ee.taltech.iti0302project.app.repository.feed;

import ee.taltech.iti0302project.app.dto.feed.FetchPostsDto;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long>, JpaSpecificationExecutor<PostEntity> {

    @Query("SELECT new ee.taltech.iti0302project.app.dto.feed.FetchPostsDto(" +
            "p.id, p.title, p.body, p.locationId, p.createdBy.username, p.createdAt, " +
            "COUNT(DISTINCT u.id), COUNT(DISTINCT c.id), false) " +
            "FROM PostEntity p " +
            "LEFT JOIN p.upvotes u " +
            "LEFT JOIN p.comments c " +
            "GROUP BY p.id, p.title, p.body, p.locationId, p.createdBy.username, p.createdAt")
    List<FetchPostsDto> findPostsWithCounts(Specification<PostEntity> spec, Pageable pageable);


}
