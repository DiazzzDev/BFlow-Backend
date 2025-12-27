package Bflow.auth.repository;

import Bflow.auth.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositoryRole extends JpaRepository<Role, UUID> {
    
}
