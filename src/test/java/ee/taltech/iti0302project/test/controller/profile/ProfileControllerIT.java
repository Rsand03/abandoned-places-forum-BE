package ee.taltech.iti0302project.test.controller.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.profile.ChangeEmailDto;
import ee.taltech.iti0302project.app.dto.profile.ChangePasswordDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfileControllerIT {

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
    void getUserProfile_userExists() throws Exception {
        UUID userId = UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d");
        mvc.perform(get("/api/profile/{userId}", userId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.email").value("test@tested.com"))
                .andExpect(jsonPath("$.points").value("0"));
    }

    @Test
    void getUserProfile_userDoesntMatchWithJwtHeader() throws Exception {
        UUID userId = UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68f");
        mvc.perform(get("/api/profile/{userId}", userId)
                        .header("Authorization", "Bearer " + userUserAuthToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateEmail_successful() throws Exception {
        UUID userId = UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d");
        ChangeEmailDto changeEmailDto = ChangeEmailDto.builder()
                .newEmail("test@gmail.com")
                .password("user")
                .build();
        mvc.perform(put("/api/profile/{userId}/updateEmail", userId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                .content(objectMapper.writeValueAsString(changeEmailDto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.points").value("0"));
    }

    @Test
    void updateEmail_userDoesntMatchWithJwtHeader() throws Exception {
        UUID userId = UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68f");
        ChangeEmailDto changeEmailDto = ChangeEmailDto.builder()
                .newEmail("test@gmail.com")
                .password("user")
                .build();
        mvc.perform(put("/api/profile/{userId}/updateEmail", userId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(changeEmailDto))
                        .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updatePassword_successful() throws Exception {
        UUID userId = UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d");
        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("user")
                .newPassword("newPassword")
                .build();
        mvc.perform(put("/api/profile/{userId}/updatePassword", userId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(changePasswordDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.email").value("test@tested.com"))
                .andExpect(jsonPath("$.points").value("0"));

        UserLoginDto loginDto = UserLoginDto.builder()
                .username("user")
                .password("newPassword")
                .build();

        assertEquals(authService.authenticateUser(loginDto).getUserId(), userId);
    }

    @Test
    void updatePassword_wrongCurrentPassword() throws Exception {
        UUID userId = UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d");
        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword")
                .build();
        mvc.perform(put("/api/profile/{userId}/updatePassword", userId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(changePasswordDto))
                        .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Current password is incorrect"));
    }

    @Test
    void updatePassword_userDoesntMatchWithJwtHeader() throws Exception {
        UUID userId = UUID.fromString("e71a1997-5f06-4b3b-b5cd-bbbcec65d68f");
        ChangePasswordDto changePasswordDto = ChangePasswordDto.builder()
                .currentPassword("user")
                .newPassword("newPassword")
                .build();
        mvc.perform(put("/api/profile/{userId}/updatePassword", userId)
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .content(objectMapper.writeValueAsString(changePasswordDto))
                        .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findUsers_success() throws Exception {
        mvc.perform(get("/api/profile/allUsers")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("minPoints", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void findUsers_noResults() throws Exception {
        mvc.perform(get("/api/profile/allUsers")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("minPoints", "300"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void findUsers_invalidPageSize() throws Exception {
        mvc.perform(get("/api/profile/allUsers")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("page", "0")
                        .param("pageSize", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findUsers_noCriteria() throws Exception {
        mvc.perform(get("/api/profile/allUsers")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void findUsers_withRoleFilter() throws Exception {
        mvc.perform(get("/api/profile/allUsers")
                        .header("Authorization", "Bearer " + userUserAuthToken)
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("role", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

}
