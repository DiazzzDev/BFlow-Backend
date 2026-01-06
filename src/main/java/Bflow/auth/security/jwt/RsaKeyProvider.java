package Bflow.auth.security.jwt;

import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

@Service
public class RsaKeyProvider {
    private final Map<String, RsaKeyPair> keys = new HashMap<>();
    private String activeKid;

    public RsaKeyProvider() {
        RsaKeyPair key = generate("key-2026-01");
        keys.put(key.kid(), key);
        activeKid = key.kid();
    }

    public RsaKeyPair getActive() {
        return keys.get(activeKid);
    }

    public RSAPublicKey getPublicKey(String kid) {
        RsaKeyPair pair = keys.get(kid);
        if (pair == null) {
            throw new SecurityException("Unknown kid");
        }
        return pair.publicKey();
    }

    public void rotate() {
        RsaKeyPair newKey = generate("key-" + System.currentTimeMillis());
        keys.put(newKey.kid(), newKey);
        activeKid = newKey.kid();
    }

    private RsaKeyPair generate(String kid) {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair kp = gen.generateKeyPair();

            return new RsaKeyPair(
                    kid,
                    kp.getPrivate(),
                    (RSAPublicKey) kp.getPublic()
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Map<String, RSAPublicKey> getAllPublicKeys() {
        Map<String, RSAPublicKey> publicKeys = new HashMap<>();

        for (Map.Entry<String, RsaKeyPair> entry : keys.entrySet()) {
            publicKeys.put(entry.getKey(), entry.getValue().publicKey());
        }

        return publicKeys;
    }

    public Map<String, RsaKeyPair> getAll() {
        return Map.copyOf(keys);
    }
}
