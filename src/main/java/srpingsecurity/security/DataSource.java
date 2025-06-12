package srpingsecurity.security;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DataSource {

    public String getUser() {
        return "user";
    }
    public Account getOwner(String name) {
        return new Account(name, false);
    }
    public String display() {
        return "display";
    }

}
