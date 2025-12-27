package Bflow.wallet;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter @Setter
public class DTORequestW {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idWallet;
    
    private String name;

    private Double value;

    private Double initialValue;
}
