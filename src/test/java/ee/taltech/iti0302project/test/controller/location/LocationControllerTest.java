package ee.taltech.iti0302project.test.controller.location;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class LocationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthService authService;

    private String userUserAuthToken;

    @BeforeEach
    void setup() {
        UserLoginDto dto = new UserLoginDto();
        dto.setUsername("user");
        dto.setPassword("user");
        userUserAuthToken = authService.authenticateUser(dto).getToken();
    }

    @Test
    void getLocationAttributes() throws Exception {
        mvc.perform(get("/api/locations/attributes").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(14))
                .andExpect(jsonPath("$.conditions.length()").value(6))
                .andExpect(jsonPath("$.statuses.length()").value(8))
                .andExpect(jsonPath("$.categories[0].name").value("Määramata"))
                .andExpect(jsonPath("$.conditions[5].name").value("Väga hea"))
                .andExpect(jsonPath("$.statuses[7].name").value("Omaniku loal ligipääsetav"));
    }

    @Test
    void getLocationStatuses() throws Exception {
        mvc.perform(get("/api/locations/statuses").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].name").value("Lammutatud"));
    }

    @Test
    void getFilteredLocations() throws Exception {
        mvc.perform((get("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("mainCategoryId", "1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("a59b74f9-d7fc-4c8e-bf47-2b060276421e"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void createLocation() {
    }

    @Test
    void deleteLocation() {
    }

    @Test
    void getLocationById() {
    }
}