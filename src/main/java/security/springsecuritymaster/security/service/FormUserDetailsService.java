package security.springsecuritymaster.security.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import security.springsecuritymaster.domain.dto.AccountContext;
import security.springsecuritymaster.domain.dto.AccountDto;
import security.springsecuritymaster.domain.entity.Account;
import security.springsecuritymaster.users.repository.UserRepository;


import java.util.List;

@Service( "userDetailsService")
@RequiredArgsConstructor
public class FormUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = userRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(account.getRoles()));
        ModelMapper mapper = new ModelMapper();
        AccountDto dto = mapper.map(account, AccountDto.class);

        return new AccountContext(dto, authorities);
    }
}
