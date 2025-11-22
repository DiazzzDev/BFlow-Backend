package Diaz.Dev.BFlow.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class ControllerWallet {
    @GetMapping
    public String connection(){
        return "Hello postgresql";
    }
}