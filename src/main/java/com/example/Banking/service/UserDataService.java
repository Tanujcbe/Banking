package com.example.Banking.service;

import com.example.Banking.model.BalanceModel;
import com.example.Banking.model.UserModel;

public interface UserDataService{
    void createBankDetails(UserModel userModel);
    void depositCash(BalanceModel balanceModel);
    void withdrawCash(BalanceModel balanceModel);
    Integer checkBalance(String userMail);
}
