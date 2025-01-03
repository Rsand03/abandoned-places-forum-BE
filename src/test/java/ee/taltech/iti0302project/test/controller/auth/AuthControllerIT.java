package ee.taltech.iti0302project.test.controller.auth;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIT {

    @Autowired
    private AuthService authService;

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserRegisterDto userUserRegisterDto;
    private UserRegisterDto adminUserRegisterDto;

    private UserLoginDto userUserLoginDto;
    private UserLoginDto adminUserLoginDto;


    @BeforeEach
    void setup() {
        userUserRegisterDto = UserRegisterDto.builder()
                .username("user")
                .password("user")
                .build();
        adminUserRegisterDto = UserRegisterDto.builder()
                .username("admin")
                .password("admin")
                .build();
        userUserLoginDto = UserLoginDto.builder()
                .username("user")
                .password("user").build();
        adminUserLoginDto = UserLoginDto.builder()
                .username("admin")
                .password("admin").build();
    }


    @Test
    void login_regularUser_successful() throws Exception {
        mvc.perform((post("/api/public/auth/login")
                        .content(objectMapper.writeValueAsString(userUserRegisterDto))
                        .contentType("application/json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.userId").value("e71a1997-5f06-4b3b-b5cd-bbbcec65d68d"))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.points").value(0));
    }

    @Test
    void login_admin_successful() throws Exception {
        mvc.perform((post("/api/public/auth/login")
                        .content(objectMapper.writeValueAsString(adminUserLoginDto))
                        .contentType("application/json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.userId").value("fdee620f-550e-4e88-9d6e-638ab6e678be"))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.points").value(0));
    }

}
