package Bflow.auth.security;

import Bflow.auth.entities.User;
import Bflow.auth.enums.AuthProvider;
import Bflow.auth.security.jwt.JwtService;
import Bflow.auth.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String providerId = oAuth2User.getAttribute("sub");

        if (email == null || providerId == null) {
            throw new IllegalStateException("Google OAuth user missing required attributes");
        }

        User user = userService.resolveOAuth2User(
                email,
                providerId,
                AuthProvider.GOOGLE
        );

        List<String> roles = user.getRoles()
                .stream()
                .map(r -> "ROLE_" + r)
                .toList();

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                roles
        );

        response.sendRedirect(
                "http://localhost:8080/login/success?token=" + token
        );
    }
}
