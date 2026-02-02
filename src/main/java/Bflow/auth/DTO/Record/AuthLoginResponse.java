package Bflow.auth.DTO.Record;

public record AuthLoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}