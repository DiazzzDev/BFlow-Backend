package Bflow.auth.controllers;

import Bflow.auth.security.jwt.RsaKeyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/keys")
public class KeyRotationController {

    private final RsaKeyProvider keyProvider;

    @PostMapping("/rotate")
    public void rotate() {
        keyProvider.rotate();
    }
}

