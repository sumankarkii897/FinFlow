package com.finflow.account.controller;
import com.finflow.account.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/me")
    public ResponseEntity<?> getAccounts() {
        return ResponseEntity.ok(
               accountService.getMyAccounts()
        );
    }

    @DeleteMapping("/close/{accountNumber}")
    public ResponseEntity<?> deleteAccount(@PathVariable("accountNumber")
                                               String accountNumber) {
        return ResponseEntity.ok(accountService.closeAccount(accountNumber));
    }


}
