package ee.taltech.iti0302project.app.service.feed;

import ee.taltech.iti0302project.app.criteria.FeedSearchCriteria;
import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.dto.feed.FetchPostsDto;
import ee.taltech.iti0302project.app.dto.location.LocationResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.FetchPostsMapper;
import ee.taltech.iti0302project.app.dto.mapper.feed.PostMapper;
import ee.taltech.iti0302project.app.dto.mapper.location.LocationMapper;
import ee.taltech.iti0302project.app.entity.feed.PostEntity;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import ee.taltech.iti0302project.app.entity.location.LocationEntity;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.pagination.PageResponse;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import ee.taltech.iti0302project.app.repository.location.LocationRepository;
import ee.taltech.iti0302project.app.specifications.PostSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class FeedService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;
    private final FetchPostsMapper fetchPostsMapper;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public CreatePostDto createPost(CreatePostDto createdPost) {
        UserEntity user = userRepository.findById(createdPost.getUserId())
                .orElseThrow(() -> new ApplicationException("User not found"));

        PostEntity entity = postMapper.toEntity(createdPost);
        entity.setLocation(locationRepository.findById(createdPost.getLocationId()).orElseThrow(
                () -> new ApplicationException("Invalid location id"))
        );

        LocationEntity location = locationRepository.findById(entity.getLocation().getId())
                .orElseThrow(() -> new ApplicationException("Location not found"));

        if (!location.isPublic()) {
            throw new ApplicationException("Location is not public");
        }

        entity.setCreatedBy(user);
        entity.setCreatedAt(LocalDate.now());

        postRepository.save(entity);

        log.info("Post {} added by {}", entity.getId(), entity.getCreatedBy());

        return postMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public Optional<FetchPostsDto> getPostById(Long postId) {
        Optional<PostEntity> postEntityOptional = postRepository.findById(postId);

        return postEntityOptional.map(fetchPostsMapper::toDto);
    }

    @Transactional(readOnly = true)
    public PageResponse<FetchPostsDto> findPosts(FeedSearchCriteria criteria, UUID currentUserId) {
        Specification<PostEntity> spec = addSpecifications(criteria);

        log.info("Search Criteria: {}", criteria);

        String sortBy = criteria.sortBy() != null ? criteria.sortBy() : "id";
        String direction = criteria.sortDirection() != null ? criteria.sortDirection().toUpperCase() : "DESC";
        int pageNumber = criteria.page() != null ? criteria.page() : 0;
        int pageSize = criteria.pageSize() != null ? criteria.pageSize() : 10;

        Sort sort = Sort.by(Sort.Direction.valueOf(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<PostEntity> entityPage = postRepository.findAll(spec, pageable);

        log.info("Post Page: {}", entityPage.getContent());

        List<FetchPostsDto> dtoList = entityPage.getContent().stream()
                .map(post -> {
                    LocationEntity locationEntity = locationRepository.findById(post.getLocation().getId())
                            .orElseThrow(() -> new ApplicationException("Location not found"));

                    FetchPostsDto dto = fetchPostsMapper.toDto(post);
                    dto.setLikeCount((long) (post.getUpvotes() != null ? post.getUpvotes().size() : 0));
                    dto.setCommentCount((long) (post.getComments() != null ? post.getComments().size() : 0));
                    dto.setHasUpvoted(hasUserUpvotedPost(post.getId(), currentUserId));

                    LocationResponseDto locationResponseDto = locationMapper.toResponseDto(locationEntity);
                    dto.setLocation(locationResponseDto);

                    return dto;
                })
                .toList();

        return new PageResponse<>(
                dtoList,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }

    private Specification<PostEntity> addSpecifications(FeedSearchCriteria criteria) {
        Specification<PostEntity> spec = Specification.where(null);

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

        return spec;
    }

    public boolean hasUserUpvotedPost(Long postId, UUID userId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException("Post not found"));

        for (UpvoteEntity upvote : post.getUpvotes()) {
            if (upvote.getUser().getId().equals(userId)) {
                return true;
            }
        }

        return false;
    }
}
