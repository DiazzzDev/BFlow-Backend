package Bflow.auth.controllers;

import Bflow.auth.DTO.AuthLoginRequest;
import Bflow.auth.DTO.AuthMeResponse;
import Bflow.auth.DTO.AuthRegisterRequest;
import Bflow.auth.DTO.Record.RefreshRotationResult;
import Bflow.auth.DTO.Record.RefreshSession;
import Bflow.auth.entities.RefreshToken;
import Bflow.auth.entities.User;
import Bflow.auth.security.jwt.JwtService;
import Bflow.auth.services.AuthService;
import Bflow.auth.services.ServiceRefreshToken;
import Bflow.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final ServiceRefreshToken serviceRefreshToken;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody AuthLoginRequest request,
            HttpServletResponse response
    ) {
        // 1. Authenticate credentials
        User user = authService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        // 2. Roles
        List<String> roles = authService.getRoles(user);

        // 3. Access token (RS256)
        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                roles
        );

        // 4. Refresh token
        String rawRefreshToken = UUID.randomUUID().toString();
        serviceRefreshToken.create(user.getId(), rawRefreshToken);

        // 5. Cookies
        setCookie(response, "access_token", accessToken, jwtService.getAccessTokenTtlSeconds());
        setCookie(response, "refresh_token", rawRefreshToken, 14 * 24 * 3600);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            serviceRefreshToken.validateAndRotate(refreshToken);
        }

        clearCookie(response, "access_token");
        clearCookie(response, "refresh_token");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody AuthRegisterRequest request, HttpServletRequest httpRequest){
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User registered successfully", null, httpRequest.getRequestURI()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthMeResponse>> me(Authentication authentication, HttpServletRequest httpRequest){
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AuthMeResponse response = new AuthMeResponse();
        response.setUserId(UUID.fromString(authentication.getName()));
        response.setRoles(authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );

        Object details = authentication.getDetails();
        if (details instanceof Map<?, ?> map) {
            response.setEmail((String) map.get("email"));
        }

        return ResponseEntity.ok(ApiResponse.success("User authenticated", response, httpRequest.getRequestURI()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RefreshRotationResult result = serviceRefreshToken.rotate(refreshToken);

        User user = authService.findById(result.userId());
        List<String> roles = authService.getRoles(user);

        String newAccessToken = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                roles
        );

        setCookie(response, "access_token", newAccessToken, jwtService.getAccessTokenTtlSeconds());
        setCookie(response, "refresh_token", result.newRefreshToken(), 14 * 24 * 3600);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<RefreshSession>> sessions(
            @CookieValue("refresh_token") String rawToken
    ) {
        RefreshToken current = serviceRefreshToken.validate(rawToken); // solo validate, NO rotate

        List<RefreshSession> sessions = serviceRefreshToken.listActiveSessions(
                        current.getUserId(),
                        current.getId()
        );

        return ResponseEntity.ok(sessions);
    }

    //Cookie util methods
    private void setCookie(HttpServletResponse res, String name, String value, long maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(maxAge)
                .build();
        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearCookie(HttpServletResponse res, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}