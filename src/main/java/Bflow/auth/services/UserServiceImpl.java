package Bflow.auth.services;

import Bflow.auth.entities.AuthAccount;
import Bflow.auth.entities.User;
import Bflow.auth.enums.AuthProvider;
import Bflow.auth.repository.RepositoryAuthAccount;
import Bflow.auth.repository.RepositoryUser;
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
                .orElseGet(() -> createOAuth2User(email, providerId, provider));
    }

    private User createOAuth2User(
            String email,
            String providerId,
            AuthProvider provider
    ) {
        User user = User.builder()
                .email(email)
                .enabled(true)
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
