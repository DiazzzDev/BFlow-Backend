package Bflow.auth.security.jwt;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.UUID;

public interface JwtService {
    String generateToken(
            UUID userId,
            String email,
            List<String> roles
    );

    long getAccessTokenTtlSeconds();

    boolean validateToken(String token);

    UUID extractUserId(String token);

    String extractEmail(String token);

    List<String> extractRoles(String token);

    void attachAuthCookies(
            HttpServletResponse response,
            String accessToken,
            String refreshToken
    );
}
