package Bflow.auth.services;

import Bflow.auth.entities.User;
import Bflow.auth.enums.AuthProvider;

import java.util.UUID;

public interface UserService {
    User findOrCreateOAuthUser(String email, AuthProvider authProvider);
    User findById(UUID id);

    User resolveOAuth2User(
            String email,
            String providerId,
            AuthProvider provider
    );
}
