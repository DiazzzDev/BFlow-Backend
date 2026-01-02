package Bflow.auth.controllers;

import Bflow.auth.DTO.AuthLoginRequest;
import Bflow.auth.DTO.AuthRegisterRequest;
import Bflow.auth.services.AuthService;
import Bflow.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody AuthLoginRequest request, HttpServletRequest httpRequest){
        authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Login successful",
                        null,
                        httpRequest.getRequestURI()
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody AuthRegisterRequest request, HttpServletRequest httpRequest){
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User registered successfully", null, httpRequest.getRequestURI()));
    }
}
