package ee.taltech.iti0302project.app.service.user;

import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.enums.user.UserPointActions;
import ee.taltech.iti0302project.app.exception.NotFoundException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserPointsService {

    public static final int USER_POINTS_FOR_PUBLISHING_A_LOCATION = 5;

    private final UserRepository userRepository;


    public Integer giveUserPoints(UserPointActions reason, UUID userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found, can't give points"));

        Integer prevPoints = userEntity.getPoints();

        if (reason.equals(UserPointActions.PUBLISH_LOCATION)) {
            userEntity.setPoints(prevPoints + USER_POINTS_FOR_PUBLISHING_A_LOCATION);
        }


        UserEntity userEntityWithNewPoints = userRepository.save(userEntity);
        return userEntityWithNewPoints.getPoints() - prevPoints;
    }

}
