package ee.taltech.iti0302project.test.controller.location;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.location.bookmark.BookmarkType;
import ee.taltech.iti0302project.app.dto.location.bookmark.LocationBookmarkCreateDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LocationBookmarkControllerIT {
    @Autowired
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AuthService authService;
    private String userUserAuthToken;

    @BeforeEach
    void authTokenSetup() {
        UserLoginDto loginDto = UserLoginDto.builder()
                .username("user")
                .password("user")
                .build();
        userUserAuthToken = authService.authenticateUser(loginDto).getToken();
    }

    @Test
    void createLocationBookmark_isCreated() throws Exception {
        LocationBookmarkCreateDto locationBookmarkCreateDto = LocationBookmarkCreateDto.builder()
                    .type(BookmarkType.valueOf("SUUR_RISK"))
                .createdByUserUuid(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"))
                .locationId(UUID.fromString("a59b74f9-d7fc-4c8e-bf47-2b060276421e")).build();

        mvc.perform(post("/api/location-bookmarks")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(locationBookmarkCreateDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Suur risk"));
    }

    @Test
    void getLocationBookmarks_filterByLocationId() throws Exception {
        mvc.perform(get("/api/location-bookmarks")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("locationId", "a59b74f9-d7fc-4c8e-bf47-2b060276421e"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getLocationBookmarks_noLocationId() throws Exception {
        mvc.perform(get("/api/location-bookmarks")
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteLocationBookmark_isDeleted() throws Exception {
        UUID locationId = UUID.fromString("a59b74f9-d7fc-4c8e-bf47-2b060276421e");
        BookmarkType bookmarkType = BookmarkType.SUUR_RISK;

        mvc.perform(delete("/api/location-bookmarks")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("locationId", locationId.toString())
                        .param("bookmarkType", bookmarkType.name()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteLocationBookmark_notFound() throws Exception {
        UUID locationId = UUID.randomUUID();
        BookmarkType bookmarkType = BookmarkType.SUUR_RISK;

        mvc.perform(delete("/api/location-bookmarks")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("locationId", locationId.toString())
                        .param("bookmarkType", bookmarkType.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createLocationBookmark_missingFields() throws Exception {
        LocationBookmarkCreateDto locationBookmarkCreateDto = LocationBookmarkCreateDto.builder()
                .type(BookmarkType.SUUR_RISK)
                .createdByUserUuid(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"))
                .build();

        mvc.perform(post("/api/location-bookmarks")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(locationBookmarkCreateDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }
}
