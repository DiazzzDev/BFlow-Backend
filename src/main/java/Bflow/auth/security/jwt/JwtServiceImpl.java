package Bflow.auth.security.jwt;


import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final PrivateKey privateKey;

    private static final long ACCESS_TOKEN_TTL_SECONDS = 3600;

    @Override
    public String generateToken(UUID userId, List<String> roles) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .claim("roles", roles)
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusSeconds(ACCESS_TOKEN_TTL_SECONDS)))
                    .issuer("bflow-api")
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.RS256),
                    claimsSet
            );

            jwt.sign(new RSASSASigner(privateKey));

            return jwt.serialize();
        }catch (Exception e){
            throw new IllegalStateException("Error while generating JWT Token", e);
        }
    }

    @Override
    public long getAccessTokenTtlSeconds() {
        return ACCESS_TOKEN_TTL_SECONDS;
    }
}
