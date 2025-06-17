package security.springsecuritymaster.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import security.springsecuritymaster.domain.entity.Account;
import security.springsecuritymaster.users.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(Account account) {
        userRepository.save(account);
    }
}
