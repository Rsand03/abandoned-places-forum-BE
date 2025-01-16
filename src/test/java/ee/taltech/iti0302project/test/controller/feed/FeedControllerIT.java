package ee.taltech.iti0302project.test.controller.feed;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FeedControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthService authService;

    private String userUserAuthToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void authTokenSetup() {
        UserLoginDto loginDto = UserLoginDto.builder()
                .username("user")
                .password("user")
                .build();
        userUserAuthToken = authService.authenticateUser(loginDto).getToken();
    }

    @Test
    void createPost_isCreated() throws Exception {
        CreatePostDto postDto = CreatePostDto.builder()
                .title("Test Post")
                .body("This is a test post")
                .userId(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"))
                .locationId(UUID.fromString("a59b74f9-d7fc-4c8e-bf47-2b060276421e")).build();

        mvc.perform(post("/api/feed/createPost")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(postDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.body").value("This is a test post"));
    }

    @Test
    void getPosts_returnsOnePost() throws Exception {
        // When & Then
        mvc.perform(get("/api/feed")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("title", "testTitle1")
                        .param("body", "testBody1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getPostById_isFound() throws Exception {
        Long postId = 1L;

        mvc.perform(get("/api/feed/{postId}", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("testTitle"))
                .andExpect(jsonPath("$.body").value("testBody"));
    }

    @Test
    void getPostById_notFound() throws Exception {
        Long postId = 999L;

        mvc.perform(get("/api/feed/{postId}", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPosts_getsResults() throws Exception {
        mvc.perform(get("/api/feed")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("title", "testTitle")
                        .param("body", "testBody"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    void getPosts_noResults() throws Exception {
        mvc.perform(get("/api/feed")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("title", "notExisting")
                        .param("body", "notExisting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void shouldReturnForbidden_whenAuthorizationHeaderIsMissing() throws Exception {
        mvc.perform(get("/api/feed"))
                .andExpect(status().isForbidden());
    }
}

