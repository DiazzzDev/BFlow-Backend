package Bflow.auth.services;

import Bflow.auth.entities.RefreshToken;
import Bflow.auth.repository.RepositoryRefreshToken;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceRefreshToken {
    private static final Duration TTL = Duration.ofDays(14);

    private final RepositoryRefreshToken repository;

    public RefreshToken create(UUID userId, String rawToken) {
        RefreshToken rt = new RefreshToken();
        rt.setId(UUID.randomUUID());
        rt.setUserId(userId);
        rt.setTokenHash(hash(rawToken));
        rt.setCreatedAt(Instant.now());
        rt.setExpiresAt(Instant.now().plus(TTL));
        rt.setRevoked(false);

        return repository.save(rt);
    }

    public RefreshToken validateAndRotate(String rawToken) {
        String hash = hash(rawToken);

        RefreshToken existing = repository.findByTokenHash(hash)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));

        if (existing.isRevoked() || existing.getExpiresAt().isBefore(Instant.now())) {
            revokeAll(existing.getUserId());
            throw new SecurityException("Refresh token reuse detected");
        }

        existing.setRevoked(true);
        repository.save(existing);

        return existing;
    }

    public void revokeAll(UUID userId) {
        repository.findAllByUserIdAndRevokedFalse(userId)
                .forEach(rt -> {
                    rt.setRevoked(true);
                    repository.save(rt);
                });
    }

    private String hash(String token) {
        return DigestUtils.sha256Hex(token);
    }

}
