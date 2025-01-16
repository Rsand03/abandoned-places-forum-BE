package ee.taltech.iti0302project.app.service.feed;

import ee.taltech.iti0302project.app.dto.feed.CreateUpvoteDto;
import ee.taltech.iti0302project.app.dto.feed.UpvoteResponseDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.UpvoteMapper;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.repository.feed.PostRepository;
import ee.taltech.iti0302project.app.repository.feed.UpvoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UpvoteService {

    private final UpvoteRepository upvoteRepository;
    private final UpvoteMapper upvoteMapper;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UpvoteResponseDto toggleUpvote(CreateUpvoteDto upvoteDto) {
        boolean hasAlreadyUpvoted = upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId());

        if (hasAlreadyUpvoted) {
            UpvoteEntity upvote = upvoteRepository.findByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())
                    .orElseThrow(() -> new ApplicationException("Upvote not found"));
            upvoteRepository.delete(upvote);

            log.info("Upvote removed from post {} by {}", upvote.getPost(), upvote.getUser());

            return upvoteMapper.toResponseDto(upvote);
        } else {
            UpvoteEntity upvote = upvoteMapper.toEntity(upvoteDto);
            upvote.setUser(userRepository.findById(upvoteDto.getUserId())
                    .orElseThrow(() -> new ApplicationException("Invalid user id")));
            upvote.setPost(postRepository.findById(upvoteDto.getPostId())
                    .orElseThrow(() -> new ApplicationException("Invalid post id")));
            upvote = upvoteRepository.save(upvote);

            log.info("Upvote added to post {} by {}", upvote.getPost(), upvote.getUser());

            return upvoteMapper.toResponseDto(upvote);
        }
    }

    @Transactional(readOnly = true)
    public List<UpvoteResponseDto> getUpvotesByPostId(Long postId) {
        List<UpvoteEntity> upvotes = upvoteRepository.findByPostId(postId);
        return upvoteMapper.toDtoList(upvotes);
    }
}
