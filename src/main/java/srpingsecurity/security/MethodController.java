package srpingsecurity.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MethodController {

    private final DataService dataService;

   @PostMapping("/writeList")
    public List<Account> writeList(@RequestBody List<Account> list) {
     return  dataService.writeList(list);
  }

    @PostMapping("/writeMap")
    public Map<String,Account> writeMap(@RequestBody List<Account> list) {

        return dataService.writeMap(list.stream().collect(Collectors.toMap(Account::getOwner, account -> account)));

    }

    @GetMapping("/readList")
    public List<Account> readList() {
        return  dataService.readList();
    }

    @GetMapping("/readMap")
    public Map<String,Account> ListMap() {
        return dataService.readMap();

    }

}
