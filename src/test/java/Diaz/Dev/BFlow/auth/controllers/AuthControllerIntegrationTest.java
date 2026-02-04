package Diaz.Dev.BFlow.auth.controllers;

import bflow.auth.DTO.AuthMeResponse;
import bflow.auth.security.jwt.JwtService;
import bflow.auth.services.AuthService;
import bflow.auth.services.ServiceRefreshToken;
import bflow.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ServiceRefreshToken serviceRefreshToken;

    private UUID testUserId;

    @BeforeEach
    void setup() {
        testUserId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGetMeEndpoint() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginWithoutAuthenticationFails() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterPublicEndpoint() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content("{\"email\":\"test@test.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testRefreshWithoutTokenFails() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogoutWithoutTokenFails() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }
}
