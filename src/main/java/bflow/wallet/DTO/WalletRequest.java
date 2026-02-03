package bflow.wallet.DTO;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Request object for creating or updating a Wallet.
 */
@Getter
@Setter
public class WalletRequest {

    /** The unique identifier of the wallet. */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID idWallet;

    /** The display name of the wallet. */
    private String name;

    /** The current balance/value of the wallet. */
    private Double value;

    /** The starting balance when the wallet was created. */
    private Double initialValue;
}
