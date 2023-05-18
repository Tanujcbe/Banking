package com.example.Banking.service.impl;

import com.example.Banking.Consts.Constants;
import com.example.Banking.model.BalanceModel;
import com.example.Banking.model.TransactionModel;
import com.example.Banking.model.UserDataModel;
import com.example.Banking.repo.AccountRepo;
import com.example.Banking.repo.TransactionRepo;
import com.example.Banking.repo.UserDataRepo;
import com.example.Banking.service.EmailService;
import com.example.Banking.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.example.Banking.utils.CurrencyConverter.convertCurrency;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    UserDataRepo userDataRepo;
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    EmailService emailService;
    @Autowired
    AccountRepo accountRepo;
    @Override
    public void transaction(TransactionModel transaction){
        log.info("Inside transactions");
        UserDataModel sourceData = userDataRepo.findUserDataModelByUserEmail(transaction.getYourAccount());
        UserDataModel targetData = userDataRepo.findUserDataModelByUserEmail(transaction.getTargetAccount());
        if(sourceData==null){
            log.info("Your account is invalid");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Your account is invalid");
        }
        if(targetData==null){
            log.info("target account is invalid");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"target account is invalid");
        }
        if(Integer.valueOf(transaction.getAmount())<0){
            log.info("Invalid amount");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid amount");
        }

        Integer amount = convertCurrency(Integer.valueOf(transaction.getAmount()),transaction.getCurrencyCode());
        Integer sourceAmount = sourceData.getBalance();
        Integer targetAmount = Integer.valueOf(targetData.getBalance());
        if(amount>sourceAmount){
            log.info("Insufficient Balance ::"+transaction.getYourAccount());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Insufficient Balance");
        }
        if(!isExceededDailyLimit(sourceData.getUserEmail(),amount)){
            log.info("Daily limit Exceeded ::"+transaction.getYourAccount());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Daily limit Exceeded");
        }
        sourceData.setBalance(sourceAmount-amount);
        targetData.setBalance(amount+targetAmount);
        transaction.setDate(new Date());
        transactionRepo.save(transaction);
        userDataRepo.save(sourceData);
        userDataRepo.save(targetData);
        emailService.debitMail(sourceData.getUserEmail(), String.valueOf(amount));
        emailService.creditMail(targetData.getUserEmail(), String.valueOf(amount));
        log.info("Transaction Completed");
    }

    @Override
    public List<TransactionModel> pastTransactions(String userMail,Date startDate,Date endDate) {
        log.info("Inside pastTransactions");
        List<TransactionModel> transactionList = transactionRepo.findByYourAccountAndDateBetween(userMail,startDate,endDate);
        transactionList.addAll(transactionRepo.findByTargetAccountAndDateBetween(userMail,startDate,endDate));
        log.info("pastTransactions Extracted");
        return transactionList;
    }
    @Override
    public List<BalanceModel> pastOfflineTransactions(String userMail, Date startDate, Date endDate) {
        log.info("Inside pastOfflineTransactions");
        List<BalanceModel> accountTransactions = accountRepo.findAllByAccountIdAndDateBetween(userMail,startDate,endDate);
        log.info("pastOfflineTransactions Extracted");
        return accountTransactions;
    }

    public Boolean isExceededDailyLimit(String accountId, Integer transferAmount) {
        log.info("Calculating DailyLimit");
        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<TransactionModel> transactionList = transactionRepo.findByYourAccountAndDateBetween(accountId, startDate, endDate);
        Integer totalAmount = 0;
        for(TransactionModel transaction :transactionList ){
            totalAmount=totalAmount+Integer.valueOf(transaction.getAmount());
        }
        return transferAmount<=(Constants.dailyTransferAmountLimit - totalAmount);
    }

}
