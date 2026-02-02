package Bflow.auth.services;

import Bflow.auth.DTO.Record.RefreshRotationResult;
import Bflow.auth.DTO.Record.RefreshSession;
import Bflow.auth.entities.RefreshToken;
import Bflow.auth.repository.RepositoryRefreshToken;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
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

        RefreshToken existing = repository.findByTokenHash(hash).orElseThrow(() -> new SecurityException("Invalid refresh token"));

        if (existing.isRevoked() || existing.getExpiresAt().isBefore(Instant.now())) {
            revokeAll(existing.getUserId());
            throw new SecurityException("Refresh token reuse detected");
        }

        existing.setRevoked(true);
        repository.save(existing);

        return existing;
    }

    public RefreshToken validate(String rawToken) {
        String hash = hash(rawToken);

        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));

        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new SecurityException("Invalid refresh token");
        }

        return token;
    }

    public RefreshRotationResult rotate(String rawToken) {
        String hash = hash(rawToken);

        RefreshToken existing = repository.findByTokenHash(hash)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            revokeAll(existing.getUserId());
            throw new SecurityException("Expired refresh token");
        }

        if (existing.isRevoked()) {
            revokeAll(existing.getUserId());
            throw new SecurityException("Refresh token reuse detected");
        }

        existing.setRevoked(true);

        String newRawToken = UUID.randomUUID().toString();
        RefreshToken replacement = new RefreshToken();
        replacement.setId(UUID.randomUUID());
        replacement.setUserId(existing.getUserId());
        replacement.setTokenHash(hash(newRawToken));
        replacement.setCreatedAt(Instant.now());
        replacement.setExpiresAt(Instant.now().plus(TTL));

        existing.setReplacedBy(replacement.getId());

        repository.save(existing);
        repository.save(replacement);

        return new RefreshRotationResult(existing.getUserId(), newRawToken);
    }

    public List<RefreshSession> listActiveSessions(
            UUID userId,
            UUID currentTokenId
    ) {
        return repository
                .findAllByUserIdAndRevokedFalseAndExpiresAtAfter(userId, Instant.now())
                .stream()
                .map(rt -> new RefreshSession(
                        rt.getId(),
                        rt.getCreatedAt(),
                        rt.getExpiresAt(),
                        rt.getId().equals(currentTokenId)
                ))
                .toList();
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
