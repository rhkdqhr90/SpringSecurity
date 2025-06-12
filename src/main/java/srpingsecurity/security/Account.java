package srpingsecurity.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Account {
    private String name;
    private boolean isSecure;
}
