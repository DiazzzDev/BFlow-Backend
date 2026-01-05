package Bflow.auth.security.jwk;

import lombok.Builder;

@Builder
public record Jwk(
        String kty,
        String kid,
        String use,
        String alg,
        String n,
        String e
) {}
