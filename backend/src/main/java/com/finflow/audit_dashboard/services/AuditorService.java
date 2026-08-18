package com.finflow.audit_dashboard.services;

import com.finflow.account.dtos.response.AccountResponse;
import com.finflow.auth_users.dtos.response.UserResponse;
import com.finflow.response.PageResponse;
import com.finflow.transaction.dtos.response.TransactionResponse;

import java.util.Map;
import java.util.Optional;

public interface AuditorService {

    Map<String , Long> getSystemTotals();
    Optional<UserResponse> findUserByEmail(String email);
    Optional<AccountResponse> findAccountDetailsByAccountNumber(String accountNumber);
    PageResponse<TransactionResponse> findTransactionsByAccountNumber(String accountNumber, int pageNumber, int pageSize);
    Optional<TransactionResponse> findTransactionsById(Long transactionId);


}
