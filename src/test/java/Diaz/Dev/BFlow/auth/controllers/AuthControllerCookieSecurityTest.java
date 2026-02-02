package Diaz.Dev.BFlow.auth.controllers;

import Bflow.auth.DTO.AuthLoginRequest;
import Bflow.auth.entities.User;
import Bflow.auth.security.jwt.JwtService;
import Bflow.auth.services.AuthService;
import Bflow.auth.services.ServiceRefreshToken;
import Bflow.auth.controllers.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerCookieSecurityTest {

    @Mock
    AuthService authService;
    @Mock
    JwtService jwtService;
    @Mock
    ServiceRefreshToken serviceRefreshToken;

    @InjectMocks
    AuthController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginSetsInsecureCookieFlags() {
        AuthLoginRequest req = new AuthLoginRequest();
        req.setEmail("a@b.com");
        req.setPassword("pass");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("a@b.com");

        when(authService.authenticate(req.getEmail(), req.getPassword())).thenReturn(user);
        when(authService.getRoles(user)).thenReturn(List.of("USER"));
        when(jwtService.generateToken(user.getId(), user.getEmail(), List.of("USER"))).thenReturn("token123");

        MockHttpServletResponse res = new MockHttpServletResponse();

        controller.login(req, res);

        var headers = res.getHeaders("Set-Cookie");
        assertTrue(headers.stream().anyMatch(h -> h.contains("access_token")));
        assertTrue(headers.stream().anyMatch(h -> h.contains("refresh_token")));

        // Current implementation sets HttpOnly and Secure to false -> insecure
        assertTrue(headers.stream().anyMatch(h -> h.contains("SameSite=None")));
        assertTrue(headers.stream().anyMatch(h -> !h.contains("HttpOnly")));
        assertTrue(headers.stream().anyMatch(h -> !h.contains("Secure")));
    }
}
