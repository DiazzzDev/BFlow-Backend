package bflow.auth.services;

import bflow.auth.entities.User;
import bflow.auth.enums.AuthProvider;

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
