package Bflow.auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthLoginResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn; //In seconds
}
