package Bflow.auth.services;

import Bflow.auth.DTO.AuthLoginRequest;
import Bflow.auth.DTO.AuthLoginResponse;
import Bflow.auth.DTO.AuthRegisterRequest;
import Bflow.auth.entities.AuthAccount;
import Bflow.auth.entities.User;
import Bflow.auth.enums.AuthProvider;
import Bflow.auth.repository.RepositoryAuthAccount;
import Bflow.auth.repository.RepositoryUser;
import Bflow.auth.repository.RepositoryUserRole;
import Bflow.auth.security.jwt.JwtService;
import Bflow.common.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RepositoryAuthAccount authAccountRepository;
    private final RepositoryUser userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RepositoryUserRole repositoryUserRole;

    public AuthLoginResponse login(@Valid AuthLoginRequest credentials) {
        AuthAccount account = authAccountRepository.findActiveByLoginAndProvider(
                        credentials.getEmail(),
                        AuthProvider.LOCAL
                ).orElseThrow(InvalidCredentialsException::new);

        if (account.getPasswordHash() == null)
            throw new InvalidCredentialsException();

        boolean matches = passwordEncoder.matches(
                credentials.getPassword(),
                account.getPasswordHash()
        );

        if (!matches)
            throw new InvalidCredentialsException();

        User user = account.getUser();
        List<String> roles = repositoryUserRole.findRoleCodesByUserId(user.getId());


        String token = jwtService.generateToken(
                user.getId(),
                roles
        );

        return new AuthLoginResponse(token, "Bearer", jwtService.getAccessTokenTtlSeconds());
    }

    public void register(@Valid AuthRegisterRequest dto) {
        boolean exists = authAccountRepository
                .existsByProviderAndProviderUserId(
                        AuthProvider.LOCAL,
                        dto.getEmail()
                );

        if (exists)
            throw new IllegalStateException("User already exists");

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
    }
}
