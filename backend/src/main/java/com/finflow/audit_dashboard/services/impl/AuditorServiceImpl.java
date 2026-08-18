package com.finflow.audit_dashboard.services.impl;

import com.finflow.account.dtos.response.AccountResponse;
import com.finflow.account.repository.AccountRepository;
import com.finflow.audit_dashboard.services.AuditorService;
import com.finflow.auth_users.dtos.response.UserResponse;
import com.finflow.auth_users.repository.UserRepository;
import com.finflow.response.PageResponse;
import com.finflow.transaction.dtos.response.TransactionResponse;
import com.finflow.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuditorServiceImpl implements AuditorService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    @Override
    public Map<String, Long> getSystemTotals() {
        long totalUsers = userRepository.count();
        long totalTransactions = transactionRepository.count();
        long totalAccounts = accountRepository.count();

        return Map.of(
                "totalUser",totalUsers,
                "totalAccounts",totalAccounts,
                "totalTransactions",totalTransactions
        );
    }

    @Override
    public Optional<UserResponse> findUserByEmail(String email) {

        return userRepository.findByEmail(email).map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Override
    public Optional<AccountResponse> findAccountDetailsByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).map(a -> modelMapper.map(a, AccountResponse.class));
    }

    @Override
    public PageResponse<TransactionResponse> findTransactionsByAccountNumber(String accountNumber, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
       Page<TransactionResponse> transactionResponsePage =
                transactionRepository.findByAccount_AccountNumber(accountNumber,pageable).map(
                        transaction -> modelMapper.map(transaction,TransactionResponse.class)
                );

        return PageResponse.<TransactionResponse>builder()
                .content(transactionResponsePage.getContent())
                .size(transactionResponsePage.getSize())
                .totalPages(transactionResponsePage.getTotalPages())
                .totalElements(transactionResponsePage.getTotalElements())
                .first(transactionResponsePage.isFirst())
                .last(transactionResponsePage.isLast())
                .page(pageNumber)
                .build();
    }

    @Override
    public Optional<TransactionResponse> findTransactionsById(Long transactionId) {
        return transactionRepository.findById(transactionId).map(transaction -> modelMapper.map(transaction, TransactionResponse.class));
    }


}
