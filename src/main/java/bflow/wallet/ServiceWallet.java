package bflow.wallet;

import bflow.wallet.DTO.WalletRequest;
import bflow.wallet.DTO.WalletResponse;
import bflow.wallet.entities.Wallet;
import bflow.wallet.entities.WalletUser;
import bflow.wallet.enums.WalletRole;
import bflow.auth.repository.RepositoryUser;
import bflow.auth.entities.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Service class for managing wallet business logic and transactions.
 */
@Service
@Transactional
@AllArgsConstructor
public class ServiceWallet {

    /** The repository for wallet database operations. */
    private final RepositoryWallet objRepoW;

    /** The repository for wallet-user relationship operations. */
    private final RepositoryWalletUser objRepoWalletUser;

    /** The repository for user database operations. */
    private final RepositoryUser objRepoUser;

    /**
     * Retrieves a wallet by its UUID for an authenticated user.
     * Validates that the user has access to the wallet through WalletUser.
     * @param walletId the UUID of the wallet to retrieve.
     * @param userId the UUID of the authenticated user.
     * @return a WalletResponse DTO containing the wallet data.
     * @throws AccessDeniedException if the user does not have access.
     */
    public WalletResponse getWalletById(
            final UUID walletId,
            final UUID userId
    ) {
        // Validate access: check if user is linked to this wallet
        WalletUser walletUser = objRepoWalletUser
                .findByWalletIdAndUserId(walletId, userId)
                .orElseThrow(() -> new AccessDeniedException(
                        "User does not have access to this wallet"
                ));

        // Retrieve the wallet from the WalletUser relationship
        Wallet wallet = walletUser.getWallet();

        // Map Wallet entity to WalletResponse DTO
        return WalletResponse.builder()
                .id(wallet.getId())
                .name(wallet.getName())
                .description(wallet.getDescription())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .initialValue(wallet.getInitialValue())
                .createdAt(wallet.getCreatedAt())
                .build();
    }

    /**
     * Creates a new wallet for an authenticated user.
     * Sets the authenticated user as OWNER through WalletUser.
     * @param request the wallet creation request.
     * @param userId the UUID of the authenticated user.
     * @return a WalletResponse DTO containing the created wallet data.
     * @throws IllegalArgumentException if the user does not exist.
     */
    public WalletResponse createWallet(
            final WalletRequest request,
            final UUID userId
    ) {
        // Retrieve authenticated user
        User user = objRepoUser.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found"
                ));

        // Create Wallet entity with proper BigDecimal handling
        Wallet wallet = new Wallet();
        wallet.setName(request.getName().trim());
        wallet.setDescription(request.getDescription());
        wallet.setCurrency(request.getCurrency());

        // Set balance = initialValue with proper scale and rounding
        BigDecimal initialValue = request.getInitialValue()
                .setScale(2, RoundingMode.HALF_EVEN);
        wallet.setInitialValue(initialValue);
        wallet.setBalance(initialValue);

        // Save Wallet
        Wallet savedWallet = objRepoW.save(wallet);

        // Create WalletUser relationship with OWNER role
        WalletUser walletUser = new WalletUser();
        walletUser.setWallet(savedWallet);
        walletUser.setUser(user);
        walletUser.setRole(WalletRole.OWNER);

        // Save WalletUser
        objRepoWalletUser.save(walletUser);

        // Map Wallet entity to WalletResponse DTO
        return WalletResponse.builder()
                .id(savedWallet.getId())
                .name(savedWallet.getName())
                .description(savedWallet.getDescription())
                .currency(savedWallet.getCurrency())
                .balance(savedWallet.getBalance())
                .initialValue(savedWallet.getInitialValue())
                .createdAt(savedWallet.getCreatedAt())
                .build();
    }
}
