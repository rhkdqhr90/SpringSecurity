package srpingsecurity.security;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DataSource {

    @PreAuthorize("hasRole('USER')")
    public String getUser() {
        System.out.println("📌 getOwner called with name =");
        return "User";
    }

    @PostAuthorize("returnObject.name == authentication.name")
    public Account getOwner(String name) {
        System.out.println("📌 getOwner called with name = " + name);
        return new Account(name,false);
    }

}
