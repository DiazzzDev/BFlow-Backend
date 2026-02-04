package bflow.auth.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Configuration class to load RSA keys from the classpath.
 */
@Configuration
public final class RSAKeyLoader {

    /**
     * Loads the RSA private key from pem file.
     * @return the loaded RSAPrivateKey.
     * @throws Exception if key loading or parsing fails.
     */
    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String key = readKey("keys/private.pem")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(spec);
    }

    /**
     * Loads the RSA public key from pem file.
     * @return the loaded RSAPublicKey.
     * @throws Exception if key loading or parsing fails.
     */
    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String key = readKey("keys/public.pem")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(spec);
    }

    /**
     * Helper to read file content from classpath.
     * @param path the resource path.
     * @return file content as string.
     * @throws Exception if stream fails.
     */
    private String readKey(final String path) throws Exception {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return new String(is.readAllBytes());
        }
    }
}
