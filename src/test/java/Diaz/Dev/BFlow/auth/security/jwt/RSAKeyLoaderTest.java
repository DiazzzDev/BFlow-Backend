package Diaz.Dev.BFlow.auth.security.jwt;

import bflow.auth.security.jwt.RSAKeyLoader;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.jupiter.api.Assertions.*;

class RSAKeyLoaderTest {

    @Test
    void loadsPrivateKeyFromResources() throws Exception {
        RSAKeyLoader loader = new RSAKeyLoader();

        RSAPrivateKey key = loader.rsaPrivateKey();
        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
    }

    @Test
    void loadsPublicKeyFromResources() throws Exception {
        RSAKeyLoader loader = new RSAKeyLoader();

        RSAPublicKey key = loader.rsaPublicKey();
        assertNotNull(key);
        assertEquals("RSA", key.getAlgorithm());
    }
}
