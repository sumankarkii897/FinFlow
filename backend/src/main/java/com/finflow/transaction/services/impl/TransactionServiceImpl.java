package com.finflow.transaction.services.impl;

import com.finflow.account.entity.Account;
import com.finflow.account.repository.AccountRepository;
import com.finflow.auth_users.entity.User;
import com.finflow.auth_users.services.UserService;
import com.finflow.enums.TransactionStatus;
import com.finflow.enums.TransactionType;
import com.finflow.exceptions.BadRequestException;
import com.finflow.exceptions.InsufficientBalanceException;
import com.finflow.exceptions.InvalidTransactionException;
import com.finflow.exceptions.NotFoundException;
import com.finflow.notification.dtos.request.NotificationRequest;
import com.finflow.notification.services.NotificationService;
import com.finflow.response.ApiResponse;
import com.finflow.response.PageResponse;
import com.finflow.transaction.dtos.request.TransactionRequest;
import com.finflow.transaction.dtos.response.TransactionResponse;
import com.finflow.transaction.entity.Transaction;
import com.finflow.transaction.repository.TransactionRepository;
import com.finflow.transaction.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ApiResponse<?> createTransaction(TransactionRequest transactionRequest) {
       Transaction transaction = Transaction.builder()
               .transactionType(transactionRequest.getTransactionType())
               .amount(transactionRequest.getAmount())
               .description(transactionRequest.getDescription())
               .build();
       switch (transactionRequest.getTransactionType()) {
           case DEPOSIT -> handleDeposit(transactionRequest,transaction);
           case WITHDRAW -> handleWithdrawal(transactionRequest,transaction);
           case TRANSFER -> handleTransfer(transactionRequest,transaction);
           default -> throw new InvalidTransactionException("Invalid transaction type");
       }
       transaction.setStatus(TransactionStatus.SUCCESS);
       Transaction savedTransaction = transactionRepository.save(transaction);

       // send notification
        sendTransactionNotification(savedTransaction);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Transaction successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<PageResponse<TransactionResponse>> getTransactionsForMyAccount(String accountNumber, int page, int size) {
        User user = userService.getCurrentLoggedInUser();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));
       if(!account.getUser().getId().equals(user.getId())) {
           throw new BadRequestException("Account does not belong to this account");
       }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "transactionDate"));

       Page<TransactionResponse> transactionResponses = transactionRepository.
               findByAccount_AccountNumber(accountNumber,pageable)
               .map(transaction -> modelMapper.map(transaction, TransactionResponse.class));

        PageResponse<TransactionResponse> pageResponse =
                PageResponse.<TransactionResponse>builder()
                        .content(transactionResponses.getContent())
                        .total(transactionResponses.getNumberOfElements())
                        .page(transactionResponses.getNumber())
                        .size(transactionResponses.getSize())
                        .totalElements(transactionResponses.getTotalElements())
                        .totalPages(transactionResponses.getTotalPages())
                        .first(transactionResponses.isFirst())
                        .last(transactionResponses.isLast())
                        .build();
        return ApiResponse.<PageResponse<TransactionResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Transactions fetched successfully")
                .timestamp(LocalDateTime.now())
                .data(pageResponse)
                .build();
    }

   private void handleDeposit(TransactionRequest transactionRequest, Transaction transaction) {
        Account account = accountRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(
                        () -> new NotFoundException("Account not found")
                );

        account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        transaction.setAccount(account);
        transactionRepository.save(transaction);
       log.info("Deposited transaction with account number {}", transaction.getAccount().getAccountNumber());
   }

   private void handleWithdrawal(TransactionRequest transactionRequest, Transaction transaction) {
        Account account = accountRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(
                        () -> new NotFoundException("Account not found")
                );
        if(account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");

        }
        account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        transaction.setAccount(account);
        transactionRepository.save(transaction);

   }

   public void handleTransfer(
           TransactionRequest transactionRequest,
                              Transaction transaction) {
        Account sourceAccount = accountRepository.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(
                        () -> new NotFoundException("Account not found")
                );

        Account destinationAccount = accountRepository.findByAccountNumber(transactionRequest.getDestinationAccountNumber())
                .orElseThrow(
                        () -> new NotFoundException("Account not found")
                );
       if (sourceAccount.getAccountNumber().equals(destinationAccount.getAccountNumber())) {
           throw new InvalidTransactionException(
                   "Source and destination accounts cannot be the same"
           );
       }
       if (transactionRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
           throw new InvalidTransactionException(
                   "Transaction amount must be greater than zero"
           );
       }
       if(sourceAccount.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
           throw new InsufficientBalanceException("Insufficient balance");

       }
       sourceAccount.setBalance(sourceAccount.getBalance().subtract(transactionRequest.getAmount()));
       destinationAccount.setBalance(destinationAccount.getBalance().add(transactionRequest.getAmount()));
//       accountRepository.save(destinationAccount);
//       transactionRepository.save(transaction);

       transaction.setAccount(sourceAccount);
       transaction.setDestinationAccount(destinationAccount.getAccountNumber());
       transaction.setSourceAccount(sourceAccount.getAccountNumber());
       transaction.setAmount(transactionRequest.getAmount());
   }

   private void sendTransactionNotification(Transaction transaction) {
       User user = transaction.getAccount().getUser();
       String subject;
       String template;

       Map<String, Object> data = new HashMap<>();
       data.put("name",user.getFirstName());
       data.put("amount",transaction.getAmount());
       data.put("accountNumber",transaction.getAccount().getAccountNumber());
       data.put("date",transaction.getTransactionDate());
       data.put("balance",transaction.getAccount().getBalance());

       if(transaction.getTransactionType()== TransactionType.DEPOSIT) {
           subject = "Credit Alert";
           template = "credit-alert";
           NotificationRequest notificationRequest = NotificationRequest.builder()
                   .recipient(user.getEmail())
                   .subject(subject)
                   .templateName(template)
                   .templateVariables(data)
                   .build();
           notificationService.sendEmail(notificationRequest,user);

       }
       else if(transaction.getTransactionType()== TransactionType.WITHDRAW) {
           subject = "Debit Alert";
           template = "debit-alert";
           NotificationRequest notificationRequest = NotificationRequest.builder()
                   .recipient(user.getEmail())
                   .subject(subject)
                   .templateName(template)
                   .templateVariables(data)
                   .build();
           notificationService.sendEmail(notificationRequest,user);

       }
       else if(transaction.getTransactionType()== TransactionType.TRANSFER) {
           subject = "Transfer Alert";
           template = "debit-alert";
           NotificationRequest notificationRequest = NotificationRequest.builder()
                   .recipient(user.getEmail())
                   .subject(subject)
                   .templateName(template)
                   .templateVariables(data)
                   .build();

           notificationService.sendEmail(notificationRequest,user);

           // receiver credit alert
           Account destinationAccount = accountRepository.findByAccountNumber(transaction.getDestinationAccount())
                   .orElseThrow(
                           () -> new NotFoundException("Account not found")
                   );
           User receiver = destinationAccount.getUser();

           Map<String, Object> receiverData = new HashMap<>();
           receiverData.put("name",receiver.getFirstName());
           receiverData.put("amount",transaction.getAmount());
           receiverData.put("accountNumber",destinationAccount.getAccountNumber());
           receiverData.put("date",transaction.getTransactionDate());
           receiverData.put("balance",transaction.getAccount().getBalance());

           NotificationRequest receiverNotification = NotificationRequest.builder()
                   .recipient(receiver.getEmail())
                   .subject("Credit Alert")
                   .templateName("credit-alert")
                   .templateVariables(receiverData)
                   .build();
           notificationService.sendEmail(receiverNotification,receiver);
       }
   }
}
