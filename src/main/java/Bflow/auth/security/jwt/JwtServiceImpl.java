package Bflow.auth.security.jwt;


import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final RsaKeyProvider rsaKeyProvider;

    private static final long ACCESS_TOKEN_TTL_SECONDS = 3600;

    @Override
    public String generateToken(UUID userId, String email, List<String> roles) {
        try {
            RsaKeyPair keys = rsaKeyProvider.getActive();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .claim("roles", roles)
                    .claim("email", email)
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusSeconds(ACCESS_TOKEN_TTL_SECONDS)))
                    .issuer("bflow-api")
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID(keys.kid())
                    .build();

            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new RSASSASigner(keys.privateKey()));

            return jwt.serialize();
        }catch (Exception e){
            throw new IllegalStateException("Error while generating JWT Token", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            String kid = jwt.getHeader().getKeyID();
            RSAPublicKey key = rsaKeyProvider.getPublicKey(kid);

            return jwt.verify(new RSASSAVerifier(key))
                    && jwt.getJWTClaimsSet()
                    .getExpirationTime()
                    .after(new Date());
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

    public void attachAuthCookies(
            HttpServletResponse response,
            String accessToken,
            String refreshToken
    ) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true) // false solo en local
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofHours(1))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
