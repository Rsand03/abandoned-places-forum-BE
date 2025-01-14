package ee.taltech.iti0302project.app.service.feed;

import ee.taltech.iti0302project.app.dto.feed.UpvoteDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.UpvoteMapper;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import ee.taltech.iti0302project.app.exception.ApplicationException;
import ee.taltech.iti0302project.app.repository.feed.UpvoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpvoteService {

    private final UpvoteRepository upvoteRepository;
    private final UpvoteMapper upvoteMapper;

    public UpvoteDto toggleUpvote(UpvoteDto upvoteDto) {
        boolean hasAlreadyUpvoted = upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId());

        if (hasAlreadyUpvoted) {
            UpvoteEntity upvote = upvoteRepository.findByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())
                    .orElseThrow(() -> new ApplicationException("Upvote not found"));
            upvoteRepository.delete(upvote);
            return upvoteDto;
        } else {
            UpvoteEntity upvote = upvoteMapper.toEntity(upvoteDto);
            upvote = upvoteRepository.save(upvote);
            return upvoteMapper.toDto(upvote);
        }
    }

    public List<UpvoteDto> getUpvotesByPostId(Long postId) {
        List<UpvoteEntity> upvotes = upvoteRepository.findByPostId(postId);
        return upvoteMapper.toDtoList(upvotes);
    }
}
