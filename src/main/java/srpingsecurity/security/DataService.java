package srpingsecurity.security;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class DataService {
    @PreFilter("filterObject.owner == authentication.name")
    public List<Account> writeList(List<Account> data){
        return data;
    }

    @PreFilter("filterObject.value.owner == authentication.name")
    public Map<String,Account> writeMap(Map<String,Account> data){
        return data;
    }

    @PostFilter("filterObject.owner == authentication.name")
    public List<Account> readList(){
        return new ArrayList<>(List.of(new Account("user","false")
        ,new Account("admin","true")
        ,new Account("db","false")));
    }

    @PostFilter("filterObject.value.owner == authentication.name")
    public HashMap<String,Account> readMap(){
        return new HashMap<>(Map.of("user",new Account("user","false")
                ,"admin",new Account("admin","true")
                ,"db",new Account("db","false")));
    }
}
