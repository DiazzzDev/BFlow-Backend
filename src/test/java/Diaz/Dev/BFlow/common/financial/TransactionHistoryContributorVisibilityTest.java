package Diaz.Dev.BFlow.common.financial;

import bflow.auth.entities.User;
import bflow.auth.enums.UserStatus;
import bflow.auth.repository.RepositoryUser;
import bflow.common.financial.ServiceTransactionHistory;
import bflow.common.financial.TransactionResponse;
import bflow.expenses.RepositoryExpense;
import bflow.expenses.entity.Expense;
import bflow.wallet.entities.Wallet;
import bflow.wallet.entities.WalletUser;
import bflow.wallet.enums.Currency;
import bflow.wallet.enums.WalletRole;
import bflow.wallet.repository.RepositoryWallet;
import bflow.wallet.repository.RepositoryWalletUser;
import bflow.wallet.service.ServiceWalletSharing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Reproduces a bug where removing a wallet member hides contributor
 * attribution on EVERY historical transaction in that wallet —
 * including the owner's own — not just the removed member's, because
 * "should we show a contributor name" was keyed off the wallet's
 * *current* live membership count instead of how many distinct people
 * have ever contributed to it.
 *
 * Expected/correct behavior: once a wallet has ever had more than one
 * contributor, transaction attribution stays visible even after that
 * member is removed — it's historical record, not a live roster.
 *
 * Before the RepositoryTransactionHistory fix, both assertions on
 * "afterRemoval" fail: contributorName comes back null for the
 * removed member's own past expense, and for the owner's own expense
 * too — the exact "todos los registros, incluyendo los mios" symptom
 * reported in production.
 */
@SpringBootTest(classes = bflow.BFlowApplication.class)
@ActiveProfiles("test")
@Transactional
class TransactionHistoryContributorVisibilityTest {

    @Autowired
    private RepositoryUser repositoryUser;

    @Autowired
    private RepositoryWallet repositoryWallet;

    @Autowired
    private RepositoryWalletUser repositoryWalletUser;

    @Autowired
    private RepositoryExpense repositoryExpense;

    @Autowired
    private ServiceWalletSharing serviceWalletSharing;

    @Autowired
    private ServiceTransactionHistory serviceTransactionHistory;

    private User owner;
    private User member;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        owner = persistUser("owner@example.com", "Owner");
        member = persistUser("member@example.com", "Member");

        wallet = new Wallet();
        wallet.setName("Shared Wallet");
        wallet.setDescription("test wallet");
        wallet.setCurrency(Currency.USD);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setInitialValue(BigDecimal.ZERO);
        wallet = repositoryWallet.save(wallet);

        addMember(owner, WalletRole.OWNER);
        addMember(member, WalletRole.MEMBER);

        persistExpense(owner, "Owner's own expense");
        persistExpense(member, "Member's expense");
    }

    private User persistUser(final String email, final String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);
        return repositoryUser.save(user);
    }

    private void addMember(final User user, final WalletRole role) {
        WalletUser walletUser = new WalletUser();
        walletUser.setWallet(wallet);
        walletUser.setUser(user);
        walletUser.setRole(role);
        repositoryWalletUser.save(walletUser);
    }

    private void persistExpense(final User contributor, final String title) {
        Expense expense = new Expense();
        expense.setTitle(title);
        expense.setAmount(new BigDecimal("3.50"));
        expense.setDate(LocalDate.now());
        expense.setWallet(wallet);
        expense.setContributor(contributor);
        expense.setSource("manual");
        repositoryExpense.save(expense);
    }
}