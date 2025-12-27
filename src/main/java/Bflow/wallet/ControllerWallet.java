package Bflow.wallet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/wallet")
@AllArgsConstructor
public class ControllerWallet {
    private final ServiceWallet objServiceW;

    @GetMapping
    public String greetings(){
        return "Welcome! If you see this, im currently preparing the proyect :)";
    }
    
}
