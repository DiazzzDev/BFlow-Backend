package Bflow.auth.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter
public class AuthMeResponse {
    private UUID userId;
    private String email;
    private List<String> roles;
}
