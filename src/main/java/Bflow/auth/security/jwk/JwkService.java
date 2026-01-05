package Bflow.auth.security.jwk;

import java.util.Map;

public interface JwkService {
    Map<String, Object> getJwks();
}