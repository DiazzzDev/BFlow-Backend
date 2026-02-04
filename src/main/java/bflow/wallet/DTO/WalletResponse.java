package bflow.wallet.DTO;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Data Transfer Object for wallet response information.
 */
@Getter
@Setter
public final class WalletResponse {

    /** The unique identifier of the wallet. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID idWallet;
}
