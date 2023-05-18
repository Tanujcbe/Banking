package com.example.Banking.service.impl;

import com.example.Banking.Consts.Constants;
import com.example.Banking.model.BalanceModel;
import com.example.Banking.model.UserDataModel;
import com.example.Banking.model.UserModel;
import com.example.Banking.repo.AccountRepo;
import com.example.Banking.repo.UserDataRepo;
import com.example.Banking.service.EmailService;
import com.example.Banking.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@Slf4j
public class UserDataServiceImpl implements UserDataService {
    @Autowired
    UserDataRepo userDataRepo;
    @Autowired
    AccountRepo accountRepo;
    @Autowired
    EmailService emailService;
    @Override
    public void createBankDetails(UserModel userModel){
        UserDataModel userDataModel = new UserDataModel();
        userDataModel.setUserEmail(userModel.getUserEmail());
        userDataModel.setBalance(0);
        userDataRepo.insert(userDataModel);
        log.info("User added in UserData");
    }

    @Override
    public void depositCash(BalanceModel balanceModel) {
        log.info("deposit money functio is triggered");
        UserDataModel userData = userDataRepo.findUserDataModelByUserEmail(balanceModel.getAccountId());
        if(userData == null){
            log.info("Account Not found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account Not available");
        }
        balanceModel.setType("DEPOSIT");
        balanceModel.setDate(new Date());
        Integer balance = Integer.valueOf(balanceModel.getBalance());
        Integer sourceAmount = Integer.valueOf(userData.getBalance());
        userData.setBalance(sourceAmount+balance);
        emailService.creditMail(userData.getUserEmail(), String.valueOf(balance));
        userDataRepo.save(userData);
        accountRepo.save(balanceModel);
        log.info("cash deposited");
    }
    @Override
    public void withdrawCash(BalanceModel balanceModel) {
        log.info("Withdraw cash function is triggered");
        UserDataModel userData = userDataRepo.findUserDataModelByUserEmail(balanceModel.getAccountId());
        if(userData == null){
            log.info("Account Not found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account Not available");
        }
        balanceModel.setType("WITHDRAW");
        balanceModel.setDate(new Date());
        Integer balance = balanceModel.getBalance();
        Integer sourceAmount = userData.getBalance();
        Integer updatedBalance = sourceAmount-balance;
        if(updatedBalance < Constants.minimumBalance){
            log.info("Less than minimum balance");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Less than minimum balance");
        }
        userData.setBalance(updatedBalance);
        userDataRepo.save(userData);
        accountRepo.save(balanceModel);
        emailService.debitMail(userData.getUserEmail(), String.valueOf(balance));
        log.info("cash Withdrawn");
    }
    public Integer checkBalance(String userMail) {
        UserDataModel userData = userDataRepo.findUserDataModelByUserEmail(userMail);
        if(userData == null){
            log.info("Account Not found");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account Not available");
        }
        return userData.getBalance();
    }
}
