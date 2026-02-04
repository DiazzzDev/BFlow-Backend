package bflow.auth.services;

import bflow.auth.entities.AuthAccount;
import bflow.auth.entities.User;
import bflow.auth.enums.AuthProvider;
import bflow.auth.repository.RepositoryAuthAccount;
import bflow.auth.repository.RepositoryUser;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public final class UserServiceImpl implements UserService {

    /** Repository for user core data. */
    private final RepositoryUser userRepository;
    /** Repository for authentication account mapping. */
    private final RepositoryAuthAccount authAccountRepository;

    @Override
    public User resolveOAuth2User(
            final String email,
            final String providerId,
            final AuthProvider provider
    ) {
        return userRepository.findByEmail(email)
                .map(existingUser -> validateProvider(existingUser, provider))
                .orElseGet(() -> createOAuth2User(email, providerId, provider));
    }

    /**
     * Ensures the user is using the same provider they registered with.
     * @param user current user.
     * @param provider attempted provider.
     * @return the validated user.
     */
    private User validateProvider(
            final User user,
            final AuthProvider provider
    ) {

        if (user.getProvider() != provider) {
            throw new IllegalStateException(
                    "User already registered with provider: "
                            + user.getProvider()
            );
        }
        return user;
    }

    private User createOAuth2User(
            final String email,
            final String providerId,
            final AuthProvider provider
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
    public User findOrCreateOAuthUser(
            final String email,
            final AuthProvider provider
    ) {

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
    public User findById(final UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
