package Bflow.auth.security.jwt;


import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final PrivateKey privateKey;
    private final RSAPublicKey publicKey;

    private static final long ACCESS_TOKEN_TTL_SECONDS = 3600;

    @Override
    public String generateToken(UUID userId, String email, List<String> roles) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .claim("roles", roles)
                    .claim("email", email)
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
    public boolean validateToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return jwt.verify(new RSASSAVerifier(publicKey))
                    && jwt.getJWTClaimsSet().getExpirationTime().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getAccessTokenTtlSeconds() {
        return ACCESS_TOKEN_TTL_SECONDS;
    }

    @Override
    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    @Override
    public String extractEmail(String token) {
        return (String) getClaims(token).getClaim("email");
    }

    @Override
    public List<String> extractRoles(String token) {
        return (List<String>) getClaims(token).getClaim("roles");
    }

    private JWTClaimsSet getClaims(String token) {
        try {
            return SignedJWT.parse(token).getJWTClaimsSet();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid JWT", e);
        }
    }
}
