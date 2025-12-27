package Bflow.wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceWallet {
    @Autowired
    private RepositoryWallet objRepoW;


    
}
