package Bflow.auth.security.jwk;

import java.util.List;

public interface JwkService {
    List<Jwk> getPublicKeys();
}