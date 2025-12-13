package Bflow.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class ControllerWallet {
    @Autowired
    private ServiceWallet objServiceW;

    @GetMapping
    public String greetings(){
        return "Welcome! If you see this, im currently preparing the proyect :)";
    }
    
}
