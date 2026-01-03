package Bflow.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RSAKeyLoader {
    @Bean
    public KeyPair loadRsaKeyPair() {
        try {
            //Leer la clave privada
            String privateKeyPEM = new String(Files.readAllBytes(
                    new ClassPathResource("keys/private.pem").getFile().toPath())) //ClassPathResource refers to the resources directory
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpecPrivate);

            //Leer la clave pública
            String publicKeyPEM = new String(Files.readAllBytes(
                    new ClassPathResource("keys/public.pem").getFile().toPath()))
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", ""); //No white spaces

            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM); //Get decoded base64 value from public key
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpecPublic);

            return new KeyPair(publicKey, privateKey); //Returns a RSA key pair
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA keys", e);
        }
    }

    @Bean
    public PrivateKey privateKey(KeyPair keyPair) {
        return keyPair.getPrivate();
    }

    @Bean
    public PublicKey publicKey(KeyPair keyPair) {
        return keyPair.getPublic();
    }
}
