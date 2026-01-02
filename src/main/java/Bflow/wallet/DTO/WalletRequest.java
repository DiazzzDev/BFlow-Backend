package Bflow.wallet.DTO;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@Getter @Setter
public class WalletRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID idWallet;
    
    private String name;

    private Double value;

    private Double initialValue;
}
