package ee.taltech.iti0302project.test.controller.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.feed.CommentDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerIT {

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
    void createComment_isCreated() throws Exception {
        Long postId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .body("Test comment")
                .createdByUsername("user")
                .createdById(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"))
                .postId(postId).build();

        mvc.perform(post("/api/feed/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body").value("Test comment"))
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.createdById").value("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"));
    }

    @Test
    void createComment_postNotFound() throws Exception {
        Long postId = 999L;

        CommentDto commentDto = CommentDto.builder()
                .body("Test comment")
                .createdByUsername("user")
                .postId(postId)
                .createdById(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d")).build();

        mvc.perform(post("/api/feed/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Post not found"));
    }

    @Test
    void createComment_userNotFound() throws Exception {
        Long postId = 1L;

        CommentDto commentDto = CommentDto.builder()
                .body("Test comment")
                .createdByUsername("noExistent")
                .postId(postId)
                .createdById(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68f")).build();


        mvc.perform(post("/api/feed/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("User not found"));
    }

    @Test
    void getCommentsByPostId_twoCommentsFound() throws Exception {
        Long postId = 1L;

        mvc.perform(get("/api/feed/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getCommentsByPostId_oneCommentsFound() throws Exception {
        Long postId = 2L;

        mvc.perform(get("/api/feed/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getCommentsByPostId_noCommentsFound() throws Exception {
        Long postId = 3L;

        mvc.perform(get("/api/feed/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
