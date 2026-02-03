package bflow.auth.services;

import bflow.auth.entities.AuthAccount;
import bflow.auth.entities.User;
import bflow.auth.enums.AuthProvider;
import bflow.auth.repository.RepositoryAuthAccount;
import bflow.auth.repository.RepositoryUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final RepositoryUser userRepository;
    private final RepositoryAuthAccount authAccountRepository;

    @Override
    public User resolveOAuth2User(
            String email,
            String providerId,
            AuthProvider provider
    ) {
        return userRepository.findByEmail(email)
                .map(existingUser -> validateProvider(existingUser, provider))
                .orElseGet(() -> createOAuth2User(email, providerId, provider));
    }

    private User validateProvider(User user, AuthProvider provider) {

        if (user.getProvider() != provider) {
            throw new IllegalStateException(
                    "User already registered with provider: " + user.getProvider()
            );
        }

        return user;
    }

    private User createOAuth2User(
            String email,
            String providerId,
            AuthProvider provider
    ) {
        User user = User.builder()
                .email(email)
                .enabled(true)
                .provider(provider)
                .roles(Set.of("USER"))
                .build();

        userRepository.save(user);

        AuthAccount account = AuthAccount.builder()
                .user(user)
                .provider(provider)
                .providerUserId(providerId)
                .build();

        authAccountRepository.save(account);

        return user;
    }

    @Override
    public User findOrCreateOAuthUser(String email, AuthProvider provider) {

        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .provider(provider)
                                .roles(Set.of("USER"))
                                .enabled(true)
                                .build()
                ));
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
