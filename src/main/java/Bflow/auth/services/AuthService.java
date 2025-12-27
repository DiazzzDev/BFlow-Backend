package Bflow.auth.services;

import Bflow.auth.DTO.AuthLoginRequest;
import Bflow.auth.DTO.AuthRegisterRequest;
import Bflow.auth.entities.AuthAccount;
import Bflow.auth.entities.User;
import Bflow.auth.enums.AuthProvider;
import Bflow.auth.repository.RepositoryAuthAccount;
import Bflow.auth.repository.RepositoryUser;
import Bflow.common.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {
    private final RepositoryAuthAccount authAccountRepository;
    private final RepositoryUser userRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(@Valid AuthLoginRequest credentials) {

        AuthAccount account = (AuthAccount) authAccountRepository.findActiveByLoginAndProvider(
                        credentials.getEmail(),
                        AuthProvider.LOCAL
                ).orElseThrow(InvalidCredentialsException::new);

        if (account.getPasswordHash() == null) {
            throw new InvalidCredentialsException();
        }

        boolean matches = passwordEncoder.matches(
                credentials.getPassword(),
                account.getPasswordHash()
        );

        if (!matches) {
            throw new InvalidCredentialsException();
        }
    }

    @Transactional
    public String register(@Valid AuthRegisterRequest dto) {
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
        user.setFullName(dto.getFullName());

        //We save the new user
        userRepository.save(user);

        AuthAccount account = new AuthAccount();
        account.setUser(user);
        account.setProvider(AuthProvider.LOCAL);
        account.setProviderUserId(dto.getEmail());
        account.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        account.setEnabled(true);

        authAccountRepository.save(account);

        return account.getPasswordHash();
    }
}
