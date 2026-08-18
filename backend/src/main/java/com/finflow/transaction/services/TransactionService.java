package com.finflow.transaction.services;

import com.finflow.response.ApiResponse;
import com.finflow.response.PageResponse;
import com.finflow.transaction.dtos.request.TransactionRequest;
import com.finflow.transaction.dtos.response.TransactionResponse;

import java.util.List;

public interface TransactionService {

    ApiResponse<?> createTransaction(TransactionRequest transactionRequest);

    ApiResponse<PageResponse<TransactionResponse>> getTransactionsForMyAccount(String accountNumber, int page, int size);

}
