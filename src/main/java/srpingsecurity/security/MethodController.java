package srpingsecurity.security;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MethodController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "admin 권한 검사 ";
    }


    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public String user() {
        return "user 권한 검사 ";
    }

    @GetMapping("/isAuthenticated")
    @PreAuthorize("isAuthenticated()")
    public String isAuthenticated() {
        return "isAuthenticated ";
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("#id == authentication.name")
    public String authentication(@PathVariable(value = "id") String id) {
        return id;
    }
    @GetMapping("/owner")
    @PostAuthorize("returnObject.owner == authentication.name")
    public Account owner( String name) {
        return new Account(name,false);
    }

    @GetMapping("/isSecure")
    @PostAuthorize("hasAnyAuthority('ROLE_ADMIN') and returnObject.isSecure")
    public Account isSecure( String name, String secure) {
        return new Account(name,"Y".equals(secure));
    }


}
