package srpingsecurity.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
    private String owner;
    private Boolean isSecure;

}
