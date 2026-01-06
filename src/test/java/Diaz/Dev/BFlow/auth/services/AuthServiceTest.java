package Diaz.Dev.BFlow.auth.services;

import Bflow.auth.DTO.AuthRegisterRequest;
import Bflow.auth.entities.AuthAccount;
import Bflow.auth.entities.User;
import Bflow.auth.enums.AuthProvider;
import Bflow.auth.repository.RepositoryAuthAccount;
import Bflow.auth.repository.RepositoryUser;
import Bflow.auth.services.AuthService;
import Bflow.common.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RepositoryAuthAccount authAccountRepository;

    @Mock
    private RepositoryUser userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticate_ok() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .roles(Set.of("USER"))
                .enabled(true)
                .build();

        AuthAccount account = AuthAccount.builder()
                .user(user)
                .provider(AuthProvider.LOCAL)
                .passwordHash("hashed")
                .enabled(true)
                .build();

        when(authAccountRepository.findActiveByLoginAndProvider(
                "test@test.com",
                AuthProvider.LOCAL
        )).thenReturn(Optional.of(account));

        when(passwordEncoder.matches("password", "hashed")).thenReturn(true);

        User result = authService.authenticate("test@test.com", "password");

        assertEquals(user, result);
    }

    @Test
    void authenticate_invalid_password() {
        User user = User.builder().build();

        AuthAccount account = AuthAccount.builder()
                .user(user)
                .provider(AuthProvider.LOCAL)
                .passwordHash("hashed")
                .enabled(true)
                .build();

        when(authAccountRepository.findActiveByLoginAndProvider(
                "test@test.com",
                AuthProvider.LOCAL
        )).thenReturn(Optional.of(account));

        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.authenticate("test@test.com", "wrong")
        );
    }

    @Test
    void register_ok() {
        AuthRegisterRequest dto = new AuthRegisterRequest();
        dto.setEmail("new@test.com");
        dto.setPassword("123456");

        when(authAccountRepository.existsByProviderAndProviderUserId(
                AuthProvider.LOCAL,
                "new@test.com"
        )).thenReturn(false);

        when(passwordEncoder.encode("123456")).thenReturn("hashed");

        authService.register(dto);

        verify(userRepository).save(any(User.class));
        verify(authAccountRepository).save(any(AuthAccount.class));
    }
}
