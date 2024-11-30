package ee.taltech.iti0302project.app.service.feed;

import ee.taltech.iti0302project.app.dto.feed.UpvoteDto;
import ee.taltech.iti0302project.app.dto.mapper.feed.UpvoteMapper;
import ee.taltech.iti0302project.app.entity.feed.UpvoteEntity;
import ee.taltech.iti0302project.app.repository.feed.UpvoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpvoteService {

    private final UpvoteRepository upvoteRepository;
    private final UpvoteMapper upvoteMapper;

    public List<UpvoteDto> getAllUpvotes() {
        return upvoteMapper.toDtoList(upvoteRepository.findAll());
    }

    public UpvoteDto getUpvoteById(Long id) {
        return upvoteMapper.toDto(upvoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Upvote not found with id: " + id)));
    }

    public UpvoteDto toggleUpvote(UpvoteDto upvoteDto) {
        boolean hasAlreadyUpvoted = upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId());

        if (hasAlreadyUpvoted) {
            UpvoteEntity upvote = upvoteRepository.findByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Upvote not found"));
            upvoteRepository.delete(upvote);
            return upvoteDto;
        } else {
            UpvoteEntity upvote = upvoteMapper.toEntity(upvoteDto);
            upvote = upvoteRepository.save(upvote);
            return upvoteMapper.toDto(upvote);
        }
    }


    public UpvoteDto createUpvote(UpvoteDto upvoteDto) {
        boolean hasAlreadyUpvoted = upvoteRepository.existsByPostIdAndUserId(upvoteDto.getPostId(), upvoteDto.getUserId());

        if (hasAlreadyUpvoted) {
            throw new IllegalStateException("User has already upvoted this post.");
        }

        UpvoteEntity upvote = upvoteMapper.toEntity(upvoteDto);
        upvote = upvoteRepository.save(upvote);
        return upvoteMapper.toDto(upvote);
    }

    public void deleteUpvote(Long id) {
        upvoteRepository.deleteById(id);
    }

    public List<UpvoteDto> getUpvotesByPostId(Long postId) {
        List<UpvoteEntity> upvotes = upvoteRepository.findByPostId(postId);
        return upvoteMapper.toDtoList(upvotes);
    }
}
