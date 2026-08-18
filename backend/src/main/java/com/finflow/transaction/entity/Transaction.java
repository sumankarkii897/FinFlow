package com.finflow.transaction.entity;

import com.finflow.account.entity.Account;
import com.finflow.enums.TransactionStatus;
import com.finflow.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @CreationTimestamp
    private LocalDateTime transactionDate;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    private String sourceAccount;

    private String destinationAccount;


}
