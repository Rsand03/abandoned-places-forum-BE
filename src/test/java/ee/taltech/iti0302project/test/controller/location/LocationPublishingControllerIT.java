package ee.taltech.iti0302project.test.controller.location;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.location.LocationPublishDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LocationPublishingControllerIT {

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuthService authService;
    private String userUserAuthToken;
    private String adminAdminAuthToken;


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

    @Test
    void publishLocation_error() throws Exception {
        LocationPublishDto invalidLocationCreateDto = LocationPublishDto.builder()
                .locationId(UUID.fromString("99ce8219-45fd-4c00-8ba5-7b84d29d7617"))
                .minRequiredPointsToView(5)
                .build();

        mvc.perform((patch("/api/locations/publishLocation")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(invalidLocationCreateDto))
                        .contentType("application/json")))
                .andExpect(status().isNotFound());
    }

}
