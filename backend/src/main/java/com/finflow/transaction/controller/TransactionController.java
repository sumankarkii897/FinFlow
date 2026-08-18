package com.finflow.transaction.controller;

import com.finflow.response.ApiResponse;
import com.finflow.response.PageResponse;
import com.finflow.transaction.dtos.request.TransactionRequest;
import com.finflow.transaction.dtos.response.TransactionResponse;
import com.finflow.transaction.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createTransaction(@Valid @RequestBody TransactionRequest request){
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getTransactionForMyAccount(@PathVariable String accountNumber,@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "5") int size){
        return ResponseEntity.ok(transactionService.getTransactionsForMyAccount(accountNumber,page,size));
    }
}
