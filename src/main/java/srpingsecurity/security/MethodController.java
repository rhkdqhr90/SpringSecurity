package srpingsecurity.security;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class MethodController {

    @GetMapping("/user")
    @Secured("ROLE_USER")
    public String user() {
        return "user";
    }

    @GetMapping("/admin")
    @RolesAllowed("ADMIN")
    public String admin() {
        return "admin";
    }

    @GetMapping("/db")
    @RolesAllowed("DB")
    public String db() {
        return "db";
    }

    @GetMapping("/permitAll")
    @PermitAll
    public String permitAll() {
        return "permitAll";
    }

    @GetMapping("/denyAll")
    @DenyAll
    public String denyAll() {
        return "denyAll";
    }

    @GetMapping("/isAdmin")
    @IsAdmin
    public String isAdmin() {
        return "isAdmin";
    }
    @GetMapping("/isOwner")
    @OwnerShip
    public Account ownerShip(String name) {
        return  new Account(name,"isOwner");
    }

    @GetMapping("/delete")
    @PreAuthorize("@myAuthorizer.isUser(#root)")
    public String delete() {
        return "delete";
    }

}
