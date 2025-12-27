package Bflow.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Role extends JpaRepository<Role, UUID> {
    
}
