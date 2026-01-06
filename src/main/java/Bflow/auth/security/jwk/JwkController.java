package Bflow.auth.security.jwk;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/.well-known")
@RequiredArgsConstructor
public class JwkController {

    private final JwkService jwkService;

    @GetMapping("/jwks.json")
    public Map<String, Object> keys() {
        return jwkService.getJwks();
    }
}
