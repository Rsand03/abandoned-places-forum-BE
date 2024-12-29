package ee.taltech.iti0302project.test.controller.location;

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

    @Test
    void getLocationAttributes() {
    }

    @Test
    void getLocationStatuses() throws Exception {
        mvc.perform(get("/api/locations/statuses").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Lammutatud"));
    }

    @Test
    void getFilteredLocations() {
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