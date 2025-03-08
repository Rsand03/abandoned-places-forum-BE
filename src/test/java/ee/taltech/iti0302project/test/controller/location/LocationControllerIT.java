package ee.taltech.iti0302project.test.controller.location;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.location.LocationCreateDto;
import ee.taltech.iti0302project.app.dto.location.LocationEditDto;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LocationControllerIT {

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuthService authService;
    private String userUserAuthToken;
    private String adminAdminAuthToken;


    private LocationEditDto defaultLocationEditDto;


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
    void locationEditDtoSetup() {
        defaultLocationEditDto = LocationEditDto.builder()
                .id(UUID.fromString("53ce8219-45fd-4c00-8ba5-7b84d29d7617"))
                .name("Updated")
                .mainCategoryId(9L)
                .subCategoryIds(List.of(1L, 2L))
                .conditionId(4L)
                .statusId(4L)
                .additionalInformation("Updated additional")
                .build();
    }


    @Test
    void getFilteredLocations_filterByMainCategory() throws Exception {
        mvc.perform((get("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("categoryIds", "1"))
                        .param("filterByMainCategoryOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("a59b74f9-d7fc-4c8e-bf47-2b060276421e"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getFilteredLocations_noParamsUser_locationsCreatedByUserAndPublicLocationsBasedOnPoints() throws Exception {
        mvc.perform((get("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    void getFilteredLocations_noParamsAdmin_locationsCreatedByAdminAndPublicLocationsBasedOnPoints() throws Exception {
        mvc.perform((get("/api/locations")
                        .header("Authorization", "Bearer " + adminAdminAuthToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
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
    void editLocation_allFieldsAreChanged() throws Exception {
        mvc.perform((patch("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultLocationEditDto))
                        .contentType("application/json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mainCategory.id").value(9L))
                .andExpect(jsonPath("$.condition.name").value("Keskpärane"))
                .andExpect(jsonPath("$.status.name").value("Vabalt ligipääsetav"))
                .andExpect(jsonPath("$.subCategories.length()").value(2))
                .andExpect(jsonPath("$.subCategories[0].id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.additionalInformation").value("Updated additional"));
    }

    @Test
    void editLocation_publicLocation_badRequest() throws Exception {
        defaultLocationEditDto.setId(UUID.fromString("a59b74f9-d7fc-4c8e-bf47-2b060276421e"));
        mvc.perform((patch("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultLocationEditDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editLocation_privateLocationButDifferentCreatedBy_badRequest() throws Exception {
        defaultLocationEditDto.setId(UUID.fromString("d43d3e9b-f1c3-467e-b70d-b9906d8507f2"));
        mvc.perform((patch("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultLocationEditDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editLocation_locationDoesNotExist_badRequest() throws Exception {
        defaultLocationEditDto.setId(UUID.fromString("d43d3e9b-1a2b-467e-b70d-b9906d8507f2"));
        mvc.perform((patch("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultLocationEditDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid user, subcategories or unauthorized to edit"));
    }

    @Test
    void editLocation_nullName_badRequest() throws Exception {
        defaultLocationEditDto.setName(null);
        mvc.perform((patch("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultLocationEditDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editLocation_invalidSubCategoryId_badRequest() throws Exception {
        defaultLocationEditDto.setSubCategoryIds(List.of(8L, 26L));
        mvc.perform((patch("/api/locations")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(defaultLocationEditDto))
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
