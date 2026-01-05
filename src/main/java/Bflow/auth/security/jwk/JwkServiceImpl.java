package Bflow.auth.security.jwk;

import Bflow.auth.security.jwt.RsaKeyPair;
import Bflow.auth.security.jwt.RsaKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwkServiceImpl implements JwkService {

    private final RsaKeyProvider rsaKeyProvider;

    @Override
    public List<Jwk> getPublicKeys() {
        return rsaKeyProvider.getAllPublicKeys()
                .entrySet()
                .stream()
                .map(e -> JwkMapper.from(e.getValue(), e.getKey()))
                .toList();
    }
}