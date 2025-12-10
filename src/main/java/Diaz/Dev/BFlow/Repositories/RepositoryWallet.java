package Diaz.Dev.BFlow.Repositories;

import Diaz.Dev.BFlow.Entities.EntityWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryWallet extends JpaRepository<EntityWallet, Long> {
}
