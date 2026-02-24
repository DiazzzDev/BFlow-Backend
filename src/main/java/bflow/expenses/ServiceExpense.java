package bflow.expenses;

import bflow.auth.entities.User;
import bflow.auth.repository.RepositoryUser;
import bflow.expenses.DTO.ExpenseRequest;
import bflow.expenses.DTO.ExpenseResponse;
import bflow.expenses.entity.Expense;
import bflow.wallet.RepositoryWalletUser;
import bflow.wallet.entities.Wallet;
import bflow.wallet.entities.WalletUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceExpense {
    private final RepositoryExpense repositoryExpense;

    /**
     * Repository for wallet user entity operations.
     */
    private final RepositoryWalletUser repositoryWalletUser;

    /**
     * Repository for user entity operations.
     */
    private final RepositoryUser repositoryUser;

    public ExpenseResponse newExpense(
            final ExpenseRequest request,
            final UUID userId
    ) {

        // Validate wallet access
        WalletUser walletUser = repositoryWalletUser
                .findByWalletIdAndUserId(request.getWalletId(), userId)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "You do not have access to this wallet"
                        )
                );

        Wallet wallet = walletUser.getWallet();

        // Get contributor
        User contributor = repositoryUser.findById(userId)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Authenticated user not found"
                        )
                );

        // Map DTO → Entity
        Expense expense = mapToEntity(request, wallet, contributor);

        // Subtract expense from wallet balance
        wallet.setBalance(
                wallet.getBalance().subtract(expense.getAmount())
        );

        Expense savedExpense = repositoryExpense.save(expense);

        return mapToResponse(savedExpense);
    }

    public void deleteExpense(
            final UUID expenseId,
            final UUID userId
    ) {

        Expense expense = repositoryExpense.findById(expenseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Expense not found")
                );

        // Validate access
        repositoryWalletUser
                .findByWalletIdAndUserId(
                        expense.getWallet().getId(),
                        userId
                )
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "You do not have access to this wallet"
                        )
                );

        Wallet wallet = expense.getWallet();

        // Subtract expense value from wallet balance
        wallet.setBalance(
                wallet.getBalance().add(expense.getAmount())
        );

        repositoryExpense.delete(expense);
    }

    /**
     * Maps ExpenseRequest → Expense entity
     */
    private Expense mapToEntity(
            final ExpenseRequest request,
            final Wallet wallet,
            final User contributor
    ) {

        Expense expense = new Expense();

        // ---- FinancialEntry fields ----
        expense.setTitle(request.getTitle().trim());
        expense.setDescription(request.getDescription());
        expense.setAmount(
                request.getAmount().setScale(2, RoundingMode.HALF_EVEN)
        );
        expense.setDate(request.getDate());
        expense.setWallet(wallet);
        expense.setContributor(contributor);

        // ---- Expense specific fields ----
        expense.setType(request.getType());
        expense.setTaxDeductible(
                Boolean.TRUE.equals(request.getTaxDeductible())
        );
        expense.setRecurring(
                Boolean.TRUE.equals(request.getRecurring())
        );
        expense.setRecurrencePattern(request.getRecurrencePattern());
        expense.setReimbursable(
                Boolean.TRUE.equals(request.getReimbursable())
        );

        return expense;
    }

    /**
     * Maps Expense → ExpenseResponse
     */
    private ExpenseResponse mapToResponse(final Expense expense) {

        ExpenseResponse response = new ExpenseResponse();

        response.setId(expense.getId().toString());
        response.setTitle(expense.getTitle());
        response.setDescription(expense.getDescription());
        response.setAmount(expense.getAmount());
        response.setDate(expense.getDate());

        response.setExpenseType(expense.getType().name());

        response.setTaxDeductible(expense.getTaxDeductible());
        response.setRecurring(expense.getRecurring());
        response.setReimbursable(expense.getReimbursable());

        response.setWalletId(expense.getWallet().getId().toString());
        response.setWalletName(expense.getWallet().getName());

        response.setContributorId(
                expense.getContributor().getId().toString()
        );
        response.setContributorName(
                expense.getContributor().getEmail()
        );

        response.setCreatedAt(expense.getCreatedAt());

        return response;
    }
}
