package com.finflow.transaction.dtos.response;

import com.finflow.enums.TransactionStatus;
import com.finflow.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;

    private BigDecimal amount;

    private TransactionStatus status;

    private TransactionType transactionType;

    private LocalDateTime transactionDate;

    private String description;

    private Long accountId;

    private String sourceAccount;
    private String destinationAccount;
}
