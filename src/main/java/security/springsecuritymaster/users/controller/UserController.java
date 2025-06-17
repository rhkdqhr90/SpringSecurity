package security.springsecuritymaster.users.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import security.springsecuritymaster.domain.dto.AccountDto;
import security.springsecuritymaster.domain.entity.Account;
import security.springsecuritymaster.users.service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @PostMapping("/signup")
    public String signup(AccountDto accountDto) {
        ModelMapper map  = new ModelMapper();
        Account account = map.map(accountDto, Account.class);
        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        userService.createUser(account);
        return "redirect:/";
    }


}
