package bflow.auth.security.jwt;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

public record RsaKeyPair(
        String kid,
        PrivateKey privateKey,
        RSAPublicKey publicKey
) {}
