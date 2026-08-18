package com.finflow.account.services.impl;

import com.finflow.account.dtos.response.AccountResponse;
import com.finflow.account.entity.Account;
import com.finflow.account.repository.AccountRepository;
import com.finflow.account.services.AccountService;
import com.finflow.auth_users.entity.User;
import com.finflow.auth_users.services.UserService;
import com.finflow.enums.AccountStatus;
import com.finflow.enums.AccountType;
import com.finflow.enums.Currency;
import com.finflow.exceptions.BadRequestException;
import com.finflow.exceptions.NotFoundException;
import com.finflow.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Random random = new Random();

private String generateAccountNumber(){
    String accountNumber;
    do{
       accountNumber = "11310017500"+(random.nextInt(9000)+1000);
    }
    while (accountRepository.findByAccountNumber(accountNumber).isPresent());
   log.info("Account number generated {}", accountNumber);
    return accountNumber;
}
    @Override
    public Account createAccount(AccountType accountType, User user) {
log.info("Inside createAccount");
        String accountNumber = generateAccountNumber();
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .user(user)
                .currency(Currency.NRP)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())

                .build();

        return accountRepository.save(account);
    }

    @Override
    public ApiResponse<List<AccountResponse>> getMyAccounts() {
    User user = userService.getCurrentLoggedInUser();
        List<AccountResponse> accounts = accountRepository.findByUserId(user.getId())
                .stream()
                .map((element) -> modelMapper.map(element, AccountResponse.class))
                .toList();

        return ApiResponse.<List<AccountResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Account list fetched Successfully")
                .data(accounts)
                .timestamp(LocalDateTime.now())

                .build();
    }

    @Override
    public ApiResponse<?> closeAccount(String accountNumber) {
    User user =  userService.getCurrentLoggedInUser();

    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow( () -> new NotFoundException("Account not found!"));
    if(!user.getAccounts().contains(account)){
        throw new NotFoundException("Account doesn't belong to this account");
    }
    if(account.getBalance().compareTo(BigDecimal.ZERO)>0){
        throw new BadRequestException("Account balance must be zero before closing");
    }
    account.setStatus(AccountStatus.CLOSED);
    account.setClosedAt(LocalDateTime.now());
    accountRepository.save(account);

    return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Account closed successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
