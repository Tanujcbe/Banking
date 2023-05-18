package com.example.Banking.service;

import com.example.Banking.model.BalanceModel;
import com.example.Banking.model.TransactionModel;

import java.util.Date;
import java.util.List;

public interface TransactionService {
    public void transaction(TransactionModel transactionModel);

    List<TransactionModel> pastTransactions(String userMail, Date startDate, Date endDate);

    List<BalanceModel> pastOfflineTransactions(String userMail, Date startDate, Date endDate);
}
