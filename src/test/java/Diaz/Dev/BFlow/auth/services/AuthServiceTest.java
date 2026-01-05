package Diaz.Dev.BFlow.auth.services;

import Bflow.auth.DTO.AuthRegisterRequest;
import Bflow.auth.entities.AuthAccount;
import Bflow.auth.entities.User;
import Bflow.auth.enums.AuthProvider;
import Bflow.auth.repository.RepositoryAuthAccount;
import Bflow.auth.repository.RepositoryUser;
import Bflow.auth.repository.RepositoryUserRole;
import Bflow.auth.security.jwt.JwtService;
import Bflow.auth.services.AuthService;
import Bflow.common.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    RepositoryAuthAccount authAccountRepository;

    @Mock
    RepositoryUserRole repositoryUserRole;

    @Mock
    RepositoryUser userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @Test
    void authenticate_success() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");

        AuthAccount account = new AuthAccount();
        account.setUser(user);
        account.setPasswordHash("hashed");

        when(authAccountRepository
                .findActiveByLoginAndProvider("test@test.com", AuthProvider.LOCAL))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        User result = authService.authenticate("test@test.com", "password");

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void authenticate_userNotFound() {
        when(authAccountRepository
                .findActiveByLoginAndProvider("x@test.com", AuthProvider.LOCAL))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate("x@test.com", "password")
        );
    }

    @Test
    void authenticate_invalidPassword() {
        User user = new User();

        AuthAccount account = new AuthAccount();
        account.setUser(user);
        account.setPasswordHash("hashed");

        when(authAccountRepository
                .findActiveByLoginAndProvider("test@test.com", AuthProvider.LOCAL))
                .thenReturn(Optional.of(account));

        when(passwordEncoder.matches("bad", "hashed"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate("test@test.com", "bad")
        );
    }

    @Test
    void getRoles_returnsRoles() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        when(repositoryUserRole.findRoleCodesByUserId(userId))
                .thenReturn(List.of("ROLE_USER", "ROLE_ADMIN"));

        List<String> roles = authService.getRoles(user);

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    void register_success() {
        AuthRegisterRequest dto = new AuthRegisterRequest();
        dto.setEmail("new@test.com");
        dto.setPassword("password");

        when(authAccountRepository.existsByProviderAndProviderUserId(
                AuthProvider.LOCAL, "new@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("password"))
                .thenReturn("hashed");

        authService.register(dto);

        verify(userRepository).save(any(User.class));
        verify(authAccountRepository).save(any(AuthAccount.class));
    }

    @Test
    void register_userAlreadyExists() {
        AuthRegisterRequest dto = new AuthRegisterRequest();
        dto.setEmail("test@test.com");

        when(authAccountRepository.existsByProviderAndProviderUserId(
                AuthProvider.LOCAL, "test@test.com"))
                .thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> authService.register(dto)
        );
    }

}
