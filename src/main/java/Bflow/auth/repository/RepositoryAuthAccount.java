package Bflow.auth.repository;

import Bflow.auth.entities.AuthAccount;
import Bflow.auth.enums.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepositoryAuthAccount extends JpaRepository<AuthAccount, UUID> {
    @Query("""
        SELECT a
        FROM AuthAccount a
        JOIN FETCH a.user u
        WHERE u.email = :login
          AND a.provider = :provider
          AND a.enabled = true
    """)
    Optional<AuthAccount> findActiveByLoginAndProvider(
            @Param("login") String login,
            @Param("provider") AuthProvider provider
    );


    boolean existsByProviderAndProviderUserId(
            AuthProvider provider,
            String providerUserId
    );
}
