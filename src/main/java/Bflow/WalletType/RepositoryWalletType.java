package Bflow.WalletType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryWalletType extends JpaRepository<EntityWalletType, Long> {
    
}
