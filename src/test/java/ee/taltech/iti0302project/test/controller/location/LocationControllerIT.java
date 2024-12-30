package ee.taltech.iti0302project.test.controller.location;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LocationControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthService authService;
    private String userUserAuthToken;

    private ObjectMapper objectMapper = new ObjectMapper();

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

    // Probably unnecessary endpoint - all functionality is covered by /location/attributes
    @Test
    void getLocationStatuses() throws Exception {
        mvc.perform(get("/api/locations/statuses").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].name").value("Lammutatud"));
    }

    @Test
    void getFilteredLocations_filterByMainCategory() throws Exception {
        mvc.perform((get("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("mainCategoryId", "1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("a59b74f9-d7fc-4c8e-bf47-2b060276421e"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getFilteredLocations_noParams_returnsAllPublicLocationsAndLocationsCreatedByUser() throws Exception {
        mvc.perform((get("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void createLocation_isCreated() throws Exception {
        LocationCreateDto validLocationCreateDto = LocationCreateDto.builder()
                .name("Valid Location")
                .lon(25.0)
                .lat(57.98888)
                .mainCategoryId(1L)
                .subCategoryIds(List.of(2L, 3L))
                .conditionId(1L)
                .statusId(2L)
                .build();
        mvc.perform((post("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(validLocationCreateDto))
                        .contentType("application/json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Valid Location"));
    }

    @Test
    void createLocation_missingCoordinates_badRequest() throws Exception {
        LocationCreateDto invalidLocationCreateDto = LocationCreateDto.builder()
                .name("Invalid Location")
                .lon(25.0)
                .lat(null)
                .mainCategoryId(1L)
                .subCategoryIds(List.of(2L, 3L))
                .conditionId(1L)
                .statusId(2L)
                .build();
        invalidLocationCreateDto.setAdditionalInformation("This is a popular park in the city center.");
        mvc.perform((post("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(invalidLocationCreateDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteLocation_isDeleted() throws Exception {
        mvc.perform((delete("/api/locations/{id}", "53ce8219-45fd-4c00-8ba5-7b84d29d7617")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteLocation_publicLocation_isNotDeleted() throws Exception {
        mvc.perform((delete("/api/locations/{id}", "a7fb00a2-6b6f-4fd2-985d-6f9aed8bdd81")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteLocation_publicLocationOfDifferentUser_isNotDeleted() throws Exception {
        mvc.perform((delete("/api/locations/{id}", "f3b814b6-cc3f-448c-bb13-2832b9b8d7e9")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteLocation_privateLocationOfDifferentUser_isNotDeleted() throws Exception {
        mvc.perform((delete("/api/locations/{id}", "d43d3e9b-f1c3-467e-b70d-b9906d8507f2")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getLocationById_privateLocationOfUser_isFound() throws Exception {
        mvc.perform((get("/api/locations/{id}", "53ce8219-45fd-4c00-8ba5-7b84d29d7617")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User user private loc 1"));
    }

    @Test
    void getLocationById_publicLocationCreatedByDifferentUser_isFound() throws Exception {
        mvc.perform((get("/api/locations/{id}", "f3b814b6-cc3f-448c-bb13-2832b9b8d7e9")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin admin public loc 1"));
    }

    @Test
    void getLocationById_privateLocationOfDifferentUser_isNotFound() throws Exception {
        mvc.perform((get("/api/locations/{id}", "d43d3e9b-f1c3-467e-b70d-b9906d8507f2")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isNotFound());
    }

}
