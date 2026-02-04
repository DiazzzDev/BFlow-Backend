package bflow.wallet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

/**
 * Service class for managing wallet business logic and transactions.
 */
@Service
@Transactional
@AllArgsConstructor
public class ServiceWallet {

    /** The repository for wallet database operations. */
    private final RepositoryWallet objRepoW;

}
