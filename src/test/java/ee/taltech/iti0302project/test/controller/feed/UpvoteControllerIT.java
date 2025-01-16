package ee.taltech.iti0302project.test.controller.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.feed.CreateUpvoteDto;
import ee.taltech.iti0302project.app.dto.feed.UpvoteResponseDto;
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
class UpvoteControllerIT {

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
    void createUpvote_isCreated() throws Exception {
        Long postId = 3L;

        UpvoteResponseDto upvoteResponseDto = UpvoteResponseDto.builder()
                .postId(postId)
                .userId(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d")).build();

        upvoteResponseDto.setPostId(postId);

        mvc.perform(post("/api/feed/upvotes", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(upvoteResponseDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value("3"))
                .andExpect(jsonPath("$.userId").value("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"));
    }

    @Test
    void createUpvote_isRemoved() throws Exception {
        Long postId = 1L;

        CreateUpvoteDto createUpvoteDto = CreateUpvoteDto.builder()
                .postId(postId)
                .userId(UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d")).build();

        createUpvoteDto.setPostId(postId);

        mvc.perform(post("/api/feed/upvotes", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(createUpvoteDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.postId").value("1"))
                .andExpect(jsonPath("$.userId").value("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"));
    }

    @Test
    void getUpvoteByPostId_returnsUpvotes() throws  Exception {
        Long postId = 1L;
        mvc.perform(get("/api/feed/upvotes/{postId}", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getUpvoteByPostId_returnsNoUpvotes() throws  Exception {
        Long postId = 3L;
        mvc.perform(get("/api/feed/upvotes/{postId}", postId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}
