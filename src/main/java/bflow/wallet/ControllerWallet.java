package bflow.wallet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.AllArgsConstructor;

/**
 * Controller for managing wallet-related operations.
 */
@RestController
@RequestMapping("/api/wallet")
@AllArgsConstructor
public class ControllerWallet {

    /** The service handling wallet business logic. */
    private final ServiceWallet objServiceW;

    /**
     * Returns a welcome message for the wallet API.
     * @return a greeting string.
     */
    @GetMapping
    public String greetings() {
        return "Welcome!";
    }
}
