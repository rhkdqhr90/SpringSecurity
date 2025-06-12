package srpingsecurity.security;

import org.springframework.stereotype.Service;

@Service
public class MyService {
    public String getUser(){
        return "User";
    }
    public String display(){
        System.out.println("display");
        return "display";
    }
}
