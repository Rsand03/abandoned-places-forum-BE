package ee.taltech.iti0302project.test.controller.feed;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.feed.CreatePostDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import ee.taltech.iti0302project.app.service.feed.FeedService;
import ee.taltech.iti0302project.app.criteria.FeedSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FeedControllerIT {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private FeedService feedService;

    private String userAuthToken;
    private UUID userId;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        UserLoginDto dto = new UserLoginDto();
        dto.setUsername("user");
        dto.setPassword("user");
        userAuthToken = authService.authenticateUser(dto).getToken();
    }

    @Test
    void createPost_isCreated() throws Exception {
        // Given
        CreatePostDto postDto = new CreatePostDto();
        postDto.setTitle("Test Post");
        postDto.setBody("This is a test post");
        postDto.setUserId(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"));
        postDto.setLocationId(UUID.fromString("a59b74f9-d7fc-4c8e-bf47-2b060276421e"));

        // When & Then
        mvc.perform(post("/api/feed/createPost")
                        .header("Authorization", "Bearer " + userAuthToken)
                        .content(objectMapper.writeValueAsString(postDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.body").value("This is a test post"));
    }

    @Test
    void getPosts_returnsPosts() throws Exception {
        // When & Then
        mvc.perform(get("/api/feed")
                        .header("Authorization", "Bearer " + userAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getPostById_isFound() throws Exception {
        // Given
        Long postId = 0L;

        // When & Then
        mvc.perform(get("/api/feed/{postId}", postId)
                        .header("Authorization", "Bearer " + userAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Post"))
                .andExpect(jsonPath("$.body").value("This is a test post"));
    }

    @Test
    void getPostById_notFound() throws Exception {
        // Given
        Long postId = 999L;

        // When & Then
        mvc.perform(get("/api/feed/{postId}", postId)
                        .header("Authorization", "Bearer " + userAuthToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPosts_noResults() throws Exception {
        // Given
        FeedSearchCriteria searchCriteria = new FeedSearchCriteria("Test", "test", UUID.randomUUID(),
                "test", LocalDate.of(2023, 5, 30),
                LocalDate.of(2024, 5, 30), "id", "DEC", 0, 10);

        // When & Then
        mvc.perform(get("/api/feed")
                        .header("Authorization", "Bearer " + userAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content.length()").value(0));
    }
}

