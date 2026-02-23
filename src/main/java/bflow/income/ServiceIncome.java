package bflow.income;

import bflow.auth.entities.User;
import bflow.auth.repository.RepositoryUser;
import bflow.income.DTO.IncomeRequest;
import bflow.income.DTO.IncomeResponse;
import bflow.income.entity.Income;
import bflow.wallet.RepositoryWalletUser;
import bflow.wallet.entities.Wallet;
import bflow.wallet.entities.WalletUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Service for managing income operations within wallets.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ServiceIncome {

    /**
     * Repository for income entity operations.
     */
    private final RepositoryIncome repositoryIncome;

    /**
     * Repository for wallet user entity operations.
     */
    private final RepositoryWalletUser repositoryWalletUser;

    /**
     * Repository for user entity operations.
     */
    private final RepositoryUser repositoryUser;

    /**
     * Creates a new income entry for the specified wallet and user.
     *
     * @param request the income request containing income details
     * @param userId the unique identifier of the authenticated user
     * @return the created income as an IncomeResponse
     * @throws AccessDeniedException if the user does not have access to
     *         the wallet
     */
    public IncomeResponse newIncome(
            final IncomeRequest request,
            final UUID userId
    ) {

        // Validate wallet access (supports shared wallets)
        WalletUser walletUser = repositoryWalletUser
                .findByWalletIdAndUserId(request.getWalletId(),
                        userId)
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

        // Map dto to entity
        Income income = mapToEntity(request, wallet, contributor);

        // Add income to wallet
        wallet.setBalance(
                wallet.getBalance().add(income.getAmount())
        );

        // Save income into database
        Income savedIncome = repositoryIncome.save(income);

        return mapToResponse(savedIncome);
    }

    /**
     * Maps an IncomeRequest DTO to an Income entity.
     *
     * @param request the income request containing income details
     * @param wallet the wallet to associate with the income
     * @param contributor the user contributing the income
     * @return the mapped Income entity
     */
    private Income mapToEntity(
            final IncomeRequest request,
            final Wallet wallet,
            final User contributor
    ) {

        Income income = new Income();

        income.setTitle(request.getTitle().trim());
        income.setDescription(request.getDescription());

        income.setAmount(
                request.getAmount().setScale(2, RoundingMode.HALF_EVEN)
        );

        income.setDate(request.getDate());
        income.setWallet(wallet);
        income.setContributor(contributor);
        income.setType(request.getType());

        income.setTaxable(Boolean.TRUE.equals(request.getTaxable()));
        income.setRecurring(Boolean.TRUE.equals(request.getRecurring()));
        income.setRecurrencePattern(request.getRecurrencePattern());

        return income;
    }

    /**
     * Maps an Income entity to an IncomeResponse DTO.
     *
     * @param income the income entity to map
     * @return the mapped IncomeResponse
     */
    private IncomeResponse mapToResponse(final Income income) {

        IncomeResponse response = new IncomeResponse();

        response.setId(income.getId().toString());
        response.setTitle(income.getTitle());
        response.setDescription(income.getDescription());
        response.setAmount(income.getAmount());
        response.setDate(income.getDate());
        response.setIncomeType(income.getType().name());

        response.setWalletId(income.getWallet().getId().toString());
        response.setWalletName(income.getWallet().getName());

        response.setContributorId(income.getContributor().getId()
                .toString());

        response.setCreatedAt(income.getCreatedAt());

        return response;
    }
}
