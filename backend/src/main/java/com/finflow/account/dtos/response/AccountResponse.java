package com.finflow.account.dtos.response;
import com.finflow.enums.AccountStatus;
import com.finflow.enums.AccountType;
import com.finflow.enums.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private AccountType accountType;

//    private UserResponse user;

    private Long userId;
    private Currency currency;

    private AccountStatus status;

//    private List<TransactionResponse> transactions;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;
}
