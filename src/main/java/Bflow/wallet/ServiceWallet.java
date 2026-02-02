package Bflow.wallet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class ServiceWallet {
    private final RepositoryWallet objRepoW;

}
