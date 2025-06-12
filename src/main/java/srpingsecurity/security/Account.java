package srpingsecurity.security;

import lombok.Data;

@Data
public class Account {
    public Account(String name, boolean isSecure) {
        this.name = name;
        this.isSecure = isSecure;
    }

    private String name;
    private boolean isSecure;
}
