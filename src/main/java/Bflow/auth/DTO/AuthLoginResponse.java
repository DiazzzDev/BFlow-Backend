package Bflow.auth.DTO;

public record AuthLoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}