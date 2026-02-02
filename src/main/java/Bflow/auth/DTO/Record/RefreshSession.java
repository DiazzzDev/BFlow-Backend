package Bflow.auth.DTO.Record;

import java.time.Instant;
import java.util.UUID;

public record RefreshSession (
        UUID id,
        Instant createdAt,
        Instant expiresAt,
        boolean current
) {}
