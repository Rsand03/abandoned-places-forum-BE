package ee.taltech.iti0302project.test.controller.location;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.location.LocationPublishDto;
import ee.taltech.iti0302project.app.entity.user.UserEntity;
import ee.taltech.iti0302project.app.exception.NotFoundException;
import ee.taltech.iti0302project.app.repository.UserRepository;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LocationPublishingControllerIT {

    public static final UUID USER_USER_UUID = UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d");
    public static final int POINTS_FOR_PUBLISHING_LOCTATION = 5;
    public static final String USER_USER_PRIVATE_LOCATION_UUID = "53ce8219-45fd-4c00-8ba5-7b84d29d7617";
    @Autowired
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    private String userUserAuthToken;
    private String adminAdminAuthToken;

    private LocationPublishDto defaultPublishDto;


    @BeforeEach
    void authTokenSetup() {
        UserLoginDto loginDto = UserLoginDto.builder()
                .username("user")
                .password("user")
                .build();
        userUserAuthToken = authService.authenticateUser(loginDto).getToken();
        loginDto.setUsername("admin");
        loginDto.setPassword("admin");
        adminAdminAuthToken = authService.authenticateUser(loginDto).getToken();
    }

    @BeforeEach
    void setUp() {
        defaultPublishDto = LocationPublishDto.builder()
                .locationId(UUID.fromString(USER_USER_PRIVATE_LOCATION_UUID))
                .minRequiredPointsToView(POINTS_FOR_PUBLISHING_LOCTATION)
                .build();
    }

    @Test
    void publishLocation_success() throws Exception {
        mvc.perform((patch("/api/locations/publish")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultPublishDto))
                        .contentType("application/json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_USER_PRIVATE_LOCATION_UUID))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    void publishLocation_success_userGetsPoints() throws Exception {
        UserEntity userEntity1 = userRepository.findById(USER_USER_UUID)
                .orElseThrow(() -> new NotFoundException("No such user"));
        Integer prevPoints = userEntity1.getPoints();


        mvc.perform((patch("/api/locations/publish")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultPublishDto))
                        .contentType("application/json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_USER_PRIVATE_LOCATION_UUID))
                .andExpect(jsonPath("$.isPublic").value(true));


        UserEntity userEntity2 = userRepository.findById(USER_USER_UUID)
                .orElseThrow(() -> new NotFoundException("No such user"));
        Integer afterPoints = userEntity2.getPoints();
        assertEquals(prevPoints + POINTS_FOR_PUBLISHING_LOCTATION, afterPoints);
    }

    @Test
    void publishLocation_nullLocationId_badRequest() throws Exception {
        defaultPublishDto.setLocationId(null);

        mvc.perform((patch("/api/locations/publish")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultPublishDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishLocation_nullMinRequiredPointsToView_badRequest() throws Exception {
        defaultPublishDto.setMinRequiredPointsToView(null);

        mvc.perform((patch("/api/locations/publish")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultPublishDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishLocation_tooHighMinRequiredPointsToView_badRequest() throws Exception {
        defaultPublishDto.setMinRequiredPointsToView(501);

        mvc.perform((patch("/api/locations/publish")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultPublishDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishLocation_locationOfOtherUser_forbidden() throws Exception {
        mvc.perform((patch("/api/locations/publish")
                        .header("Authorization", "Bearer " + adminAdminAuthToken)
                        .content(objectMapper.writeValueAsString(defaultPublishDto))
                        .contentType("application/json")))
                .andExpect(status().isForbidden());
    }

}
