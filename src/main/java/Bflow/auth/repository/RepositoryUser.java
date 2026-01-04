package Bflow.auth.repository;

import Bflow.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepositoryUser extends JpaRepository<User, UUID> {
    Optional<User> findById(UUID id);
}
