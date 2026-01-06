package Bflow.auth.services;

import Bflow.auth.DTO.AuthRegisterRequest;
import Bflow.auth.entities.AuthAccount;
import Bflow.auth.entities.User;
import Bflow.auth.enums.AuthProvider;
import Bflow.auth.repository.RepositoryAuthAccount;
import Bflow.auth.repository.RepositoryUser;
import Bflow.common.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RepositoryAuthAccount authAccountRepository;
    private final RepositoryUser userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(String email, String password) {

        AuthAccount account = authAccountRepository
                .findActiveByLoginAndProvider(email, AuthProvider.LOCAL)
                .orElseThrow(InvalidCredentialsException::new);

        if (account.getPasswordHash() == null ||
                !passwordEncoder.matches(password, account.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return account.getUser();
    }

    /**
     * Los roles ahora viven en User
     */
    public List<String> getRoles(User user) {
        return List.copyOf(user.getRoles());
    }

    public void register(@Valid AuthRegisterRequest dto) {

        boolean exists = authAccountRepository
                .existsByProviderAndProviderUserId(
                        AuthProvider.LOCAL,
                        dto.getEmail()
                );

        if (exists) {
            throw new IllegalStateException("User already exists");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setProvider(AuthProvider.LOCAL);
        user.setRoles(Set.of("USER"));
        user.setEnabled(true);

        userRepository.save(user);

        AuthAccount account = new AuthAccount();
        account.setUser(user);
        account.setProvider(AuthProvider.LOCAL);
        account.setProviderUserId(dto.getEmail());
        account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        account.setEnabled(true);

        authAccountRepository.save(account);
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }
}
