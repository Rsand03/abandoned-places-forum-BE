package ee.taltech.iti0302project.test.service.user;

import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.enums.user.UserPointActions;
import ee.taltech.iti0302project.app.exception.NotFoundException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.service.user.UserPointsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserPointsServiceTest {

    private static final UUID USER_UUID = UUID.fromString("68ce8219-45fd-4c01-8ba5-7b84d39d7617");
    private static final int INITIAL_POINTS = 100;
    public static final int POINTS_FOR_PUBLISHING_LOCATION = 5;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPointsService userPointsService;


    private UserEntity defaultUserEntity;


    @BeforeEach
    void setUp() {
        defaultUserEntity = new UserEntity();
        defaultUserEntity.setId(USER_UUID);
        defaultUserEntity.setPoints(INITIAL_POINTS);
    }

    @Test
    void giveUserPoints_success() {
        // Given
        given(userRepository.findById(defaultUserEntity.getId()))
                .willReturn(Optional.of(defaultUserEntity));
        given(userRepository.save(defaultUserEntity))
                .willReturn(defaultUserEntity);

        // When
        Integer addedPoints = userPointsService.giveUserPoints(UserPointActions.PUBLISH_LOCATION, USER_UUID);

        // Then
        assertEquals(POINTS_FOR_PUBLISHING_LOCATION, addedPoints);
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void giveUserPoints_userDoesNotExist_error() {
        // Given
        given(userRepository.findById(defaultUserEntity.getId()))
                .willReturn(Optional.empty());

        // When

        Throwable thrown = catchThrowable(() ->
                userPointsService.giveUserPoints(UserPointActions.PUBLISH_LOCATION, USER_UUID));

        // Then
        assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found, can't give points");
        then(userRepository).shouldHaveNoMoreInteractions();
    }

}
