package Bflow.auth.DTO.Record;

import java.util.UUID;

public record RefreshRotationResult(
        UUID userId,
        String newRefreshToken
){}
