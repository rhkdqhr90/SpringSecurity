package srpingsecurity.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/method")
    public String index() {
        return "method";
    }
}
