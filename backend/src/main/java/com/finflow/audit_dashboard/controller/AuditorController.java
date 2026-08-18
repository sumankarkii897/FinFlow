package com.finflow.audit_dashboard.controller;

import com.finflow.account.dtos.response.AccountResponse;
import com.finflow.audit_dashboard.services.AuditorService;
import com.finflow.auth_users.dtos.response.UserResponse;
import com.finflow.response.PageResponse;
import com.finflow.transaction.dtos.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit")
@PreAuthorize("hasAnyAuthority('ADMIN', 'AUDITOR')")
public class AuditorController {
    private final AuditorService auditorService;

    @GetMapping("/totals")
    public ResponseEntity<Map<String,Long>> getSystemTotals() {
        return ResponseEntity.ok(auditorService.getSystemTotals());
    }

    @GetMapping("/users")
    public ResponseEntity<UserResponse> findUserByEmail(@RequestParam String email) {
       Optional<UserResponse> userResponse = auditorService.findUserByEmail(email);
        return userResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/accounts")
    public ResponseEntity<AccountResponse> findAccountDetailsByAccountNumber(@RequestParam String accountNumber) {
        Optional<AccountResponse> accountResponse = auditorService.findAccountDetailsByAccountNumber(accountNumber);
        return accountResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/transactions/by-account")
    public ResponseEntity<PageResponse<TransactionResponse>> findTransactionsByAccountNumber(@RequestParam String accountNumber, @RequestParam(defaultValue = "0") int pageNumber,@RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<TransactionResponse> transactions = auditorService.findTransactionsByAccountNumber(accountNumber, pageNumber, pageSize);
        if(transactions.getTotalElements() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(auditorService.findTransactionsByAccountNumber(accountNumber, pageNumber, pageSize));
    }

    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<TransactionResponse> findTransactionsById(@PathVariable Long transactionId) {
        Optional<TransactionResponse> transactionResponse = auditorService.findTransactionsById(transactionId);
        return transactionResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
