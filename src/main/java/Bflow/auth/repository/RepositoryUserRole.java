package Bflow.auth.repository;

import Bflow.auth.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepositoryUserRole extends JpaRepository<UserRole, UUID> {
    @Query("""
        SELECT ur.role.name
        FROM UserRole ur
        WHERE ur.user.id = :userId
    """)
    List<String> findRoleCodesByUserId(UUID userId);
}
