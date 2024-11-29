package ee.taltech.iti0302project.app.service.feed;

import ee.taltech.iti0302project.app.criteria.FeedSearchCriteria;
import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.dto.feed.FetchPostsDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.FetchPostsMapper;
import ee.taltech.iti0302project.app.dto.mapper.feed.PostMapper;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.pagination.PageResponse;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import ee.taltech.iti0302project.app.specifications.PostSpecifications;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class FeedService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FeedService.class);

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final FetchPostsMapper fetchPostsMapper;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public CreatePostDto createPost(CreatePostDto createdPost) {
        UserEntity user = userRepository.findById(createdPost.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostEntity entity = postMapper.toEntity(createdPost);

        entity.setCreatedBy(user);

        entity.setCreatedAt(LocalDate.now());

        postRepository.save(entity);

        return postMapper.toDto(entity);
    }

    public PageResponse<FetchPostsDto> findPosts(FeedSearchCriteria criteria) {
        Specification<PostEntity> spec = Specification.where(null);

        logger.info("Search Criteria: {}", criteria);

        if (criteria.createdDateFrom() != null || criteria.createdDateTo() != null) {
            spec = spec.and(PostSpecifications.postedBetween(criteria.createdDateFrom(), criteria.createdDateTo()));
        }

        if (criteria.title() != null && !criteria.title().isEmpty()) {
            spec = spec.and(PostSpecifications.hasTitle(criteria.title()));
        }

        if (criteria.body() != null && !criteria.body().isEmpty()) {
            spec = spec.and(PostSpecifications.hasBody(criteria.body()));
        }

        if (criteria.createdByUsername() != null && !criteria.createdByUsername().isEmpty()) {
            spec = spec.and(PostSpecifications.createdByUsername(criteria.createdByUsername()));
        }

        String sortBy = criteria.sortBy() != null ? criteria.sortBy() : "id";
        String direction = criteria.sortDirection() != null ? criteria.sortDirection().toUpperCase() : "DESC";
        int pageNumber = criteria.page() != null ? criteria.page() : 0;
        int pageSize = criteria.pageSize() != null ? criteria.pageSize() : 10;

        Sort sort = Sort.by(Sort.Direction.valueOf(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        List<FetchPostsDto> content = postRepository.findPostsWithCounts(spec, pageable);

        int totalElements = content.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalElements);
        content = content.subList(fromIndex, toIndex);

        return new PageResponse<>(content, pageNumber, pageSize, totalElements, totalPages);
    }
}
