package Bflow.auth.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthLoginRequest {
    @Email
    @Size(min = 5, max = 255, message = "Email must be between 5 to 255 characters")
    private String email;

    @Size(max = 255, message = "Password cannot be longer than 255 characters")
    private String password;
}
