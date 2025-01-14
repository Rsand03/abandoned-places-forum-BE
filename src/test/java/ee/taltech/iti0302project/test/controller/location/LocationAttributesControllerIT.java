package ee.taltech.iti0302project.test.controller.location;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LocationAttributesControllerIT {

    @Autowired
    private MockMvc mvc;


    @Test
    void getLocationAttributes() throws Exception {
        mvc.perform(get("/api/locations/attributes")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(14))
                .andExpect(jsonPath("$.conditions.length()").value(6))
                .andExpect(jsonPath("$.statuses.length()").value(8))
                .andExpect(jsonPath("$.categories[0].name").value("Määramata"))
                .andExpect(jsonPath("$.conditions[5].name").value("Väga hea"))
                .andExpect(jsonPath("$.statuses[7].name").value("Omaniku loal ligipääsetav"));
    }

}
