package Diaz.Dev.BFlow.auth.security.jwt;

import bflow.auth.security.jwt.RSAKeyLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RSAKeyLoaderTest {

    @Test
    void loadsPrivateKeyFromResources() throws Exception {
        RSAKeyLoader loader = new RSAKeyLoader();

        assertNotNull(loader.rsaPrivateKey());
    }
}
