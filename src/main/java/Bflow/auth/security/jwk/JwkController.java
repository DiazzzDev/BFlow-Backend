package Bflow.auth.security.jwk;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwkController {

    private final JwkService jwkService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return Map.of("keys", jwkService.getPublicKeys());
    }
}
