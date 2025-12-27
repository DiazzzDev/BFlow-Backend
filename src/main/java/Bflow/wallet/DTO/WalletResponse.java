package Bflow.wallet.DTO;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter @Setter
public class WalletResponse {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idWallet;
}
