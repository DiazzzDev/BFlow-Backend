package Diaz.Dev.BFlow.Services;

import Diaz.Dev.BFlow.Repositories.RepositoryWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceWallet {
    @Autowired
    private RepositoryWallet objRepoW;
}
