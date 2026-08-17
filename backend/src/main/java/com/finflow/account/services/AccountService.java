package com.finflow.account.services;

import com.finflow.account.dtos.response.AccountResponse;
import com.finflow.account.entity.Account;
import com.finflow.auth_users.entity.User;
import com.finflow.enums.AccountType;
import com.finflow.response.ApiResponse;

import java.util.List;

public interface AccountService {
    Account createAccount(AccountType accountType, User user);
    ApiResponse<List<AccountResponse>> getMyAccounts();
    ApiResponse<?> closeAccount(String accountNumber);

}
