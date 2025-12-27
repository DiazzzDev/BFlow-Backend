package Bflow.wallet.DTO;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@Getter @Setter
public class WalletResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID idWallet;
}
