package Bflow.auth.security.jwk;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class JwkMapper {

    public static Jwk from(RSAPublicKey key, String kid) {
        return Jwk.builder()
                .kty("RSA")
                .kid(kid)
                .use("sig")
                .alg("RS256")
                .n(base64Url(key.getModulus().toByteArray()))
                .e(base64Url(key.getPublicExponent().toByteArray()))
                .build();
    }

    private static String base64Url(byte[] value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value);
    }
}