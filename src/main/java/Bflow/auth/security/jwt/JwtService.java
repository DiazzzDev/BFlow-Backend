package Bflow.auth.security.jwt;

import java.util.List;
import java.util.UUID;

public interface JwtService {
    String generateToken(
            UUID userId,
            List<String> roles
    );

    long getAccessTokenTtlSeconds();
}
