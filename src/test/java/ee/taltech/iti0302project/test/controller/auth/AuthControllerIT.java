package ee.taltech.iti0302project.test.controller.auth;

import ee.taltech.iti0302project.app.dto.auth.UserLoginDto;
import ee.taltech.iti0302project.app.dto.auth.UserRegisterDto;
import ee.taltech.iti0302project.app.service.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIT {

    private static final String USER_USER_ID = "e71a1997-5f06-4b3b-b5cd-bbbcec65d68d";
    private static final String USER_USER_EMAIL = "test@tested.com";
    private static final String ADMIN_ADMIN_UUID = "fdee620f-550e-4e88-9d6e-638ab6e678be";


    private static final String USERNAME_SIZE_ERROR_MESSAGE = "size must be between 3 and 30";
    private static final String PASSWORD_SIZE_ERROR_MESSAGE = "size must be between 4 and 30";
    private static final String EMAIL_SIZE_ERROR_MESSAGE = "size must be between 0 and 50";

    private static final String USERNAME_INVALID_CHAR_ERROR_MSG = "Username can only contain letters," +
            " numbers, spaces and special characters: '.', '_', and '-'.";
    private static final String PASSWORD_INVALID_CHAR_ERROR_MSG = "Password can only contain letters," +
            " numbers and special characters: !@#$%^&*()_+-=[]{};':\"|,.<>?/`~";


    @Autowired
    private JwtService jwtService;

    @Autowired
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserRegisterDto registerDto;
    private UserLoginDto userUserLoginDto;
    private UserLoginDto adminUserLoginDto;


    @BeforeEach
    void setup() {
        registerDto = UserRegisterDto.builder()
                .username("valid username")
                .password("validPassword")
                .email("valid@taltech.ee")
                .build();
        userUserLoginDto = UserLoginDto.builder()
                .username("user")
                .password("user").build();
        adminUserLoginDto = UserLoginDto.builder()
                .username("admin")
                .password("admin").build();
    }

    @Test
    void register_successful() throws Exception {
        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.userId").isString())
                .andExpect(jsonPath("$.username").value("valid username"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.points").value(0));
    }

    @Test
    void register_returnsValidAndParseableJwt() throws Exception {
        MvcResult result = mvc.perform(post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        String token = objectMapper.readTree(responseJson).get("token").asText();
        String authHeader = "Bearer " + token;

        UUID userIdFromToken = jwtService.extractUserIdFromAuthHeader(authHeader);
        UUID userIdFromResponse = UUID.fromString(objectMapper.readTree(responseJson).get("userId").asText());

        assertEquals(userIdFromResponse, userIdFromToken);
    }

    @Test
    void register_nullUsername_badRequest() throws Exception {
        registerDto.setUsername(null);

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("must not be null"));
    }

    @Test
    void register_tooShortUsername_badRequest() throws Exception {
        registerDto.setUsername("aa");

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value(USERNAME_SIZE_ERROR_MESSAGE));
    }

    @Test
    void register_tooLongUsername_badRequest() throws Exception {
        registerDto.setUsername("long long long long long long long long long long long");

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value(USERNAME_SIZE_ERROR_MESSAGE));
    }

    @Test
    void register_invalidCharacterUsername_badRequest() throws Exception {
        registerDto.setUsername("\uD83D\uDC4D\uD83D\uDC4D\uD83D\uDC96\uD83D\uDC96");  // ascii emoji

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value(USERNAME_INVALID_CHAR_ERROR_MSG));
    }

    @Test
    void register_nullPassword_badRequest() throws Exception {
        registerDto.setPassword(null);

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("must not be null"));
    }

    @Test
    void register_tooShortPassword_badRequest() throws Exception {
        registerDto.setPassword("aa");

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value(PASSWORD_SIZE_ERROR_MESSAGE));
    }

    @Test
    void register_tooLongPassword_badRequest() throws Exception {
        registerDto.setPassword("toolongtoolongtoolongtoolongtoolong");

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value(PASSWORD_SIZE_ERROR_MESSAGE));
    }

    @Test
    void register_invalidCharacterPassword_badRequest() throws Exception {
        registerDto.setPassword("invalid space");

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value(PASSWORD_INVALID_CHAR_ERROR_MSG));
    }

    @Test
    void register_nullEmail_badRequest() throws Exception {
        registerDto.setEmail(null);

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("must not be null"));
    }

    @Test
    void register_tooLongEmail_badRequest() throws Exception {
        registerDto.setEmail("a51charsemail@eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee.ee");

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value(EMAIL_SIZE_ERROR_MESSAGE));
    }

    @Test
    void register_invalidEmail_badRequest() throws Exception {
        registerDto.setEmail("ivalid@invalid.");

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email"));
    }

    @Test
    void register_usernameAlreadyInUse_badRequest() throws Exception {
        registerDto.setUsername("user");
        registerDto.setPassword("user");

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username is already in use."));
    }

    @Test
    void register_emailAlreadyInUse_badRequest() throws Exception {
        registerDto.setEmail(USER_USER_EMAIL);

        mvc.perform((post("/api/public/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType("application/json")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already in use."));
    }

    @Test
    void login_returnsValidAndParseableJwt() throws Exception {
        MvcResult result = mvc.perform(post("/api/public/auth/login")
                        .content(objectMapper.writeValueAsString(userUserLoginDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).get("token").asText();
        String authHeader = "Bearer " + token;

        UUID userIdFromToken = jwtService.extractUserIdFromAuthHeader(authHeader);
        assertEquals(UUID.fromString(USER_USER_ID), userIdFromToken);
    }

    @Test
    void login_regularUser_successful() throws Exception {
        mvc.perform((post("/api/public/auth/login")
                        .content(objectMapper.writeValueAsString(userUserLoginDto))
                        .contentType("application/json")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.userId").value(USER_USER_ID))
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
                .andExpect(jsonPath("$.userId").value(ADMIN_ADMIN_UUID))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.points").value(0));
    }

    @Test
    void login_userDoesNotExist_unAuthorized() throws Exception {
        userUserLoginDto.setUsername("nonexistent");

        mvc.perform((post("/api/public/auth/login")
                        .content(objectMapper.writeValueAsString(userUserLoginDto))
                        .contentType("application/json")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Incorrect username or password"));
    }

    @Test
    void login_wrongPassword_unAuthorized() throws Exception {
        userUserLoginDto.setPassword("wrongPassword");

        mvc.perform((post("/api/public/auth/login")
                        .content(objectMapper.writeValueAsString(userUserLoginDto))
                        .contentType("application/json")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Incorrect username or password"));
    }

}
