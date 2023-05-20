package com.example.Banking.service.impl;

import com.example.Banking.model.TransactionModel;
import com.example.Banking.model.UserDataModel;
import com.example.Banking.repo.AccountRepo;
import com.example.Banking.repo.TransactionRepo;
import com.example.Banking.repo.UserDataRepo;
import com.example.Banking.service.EmailService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class TransactionServiceImplTests {

    @Mock
    private UserDataRepo userDataRepo;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private AccountRepo accountRepo;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTransactionValidAccounts() {
        TransactionModel transaction = new TransactionModel();
        transaction.setYourAccount("source@example.com");
        transaction.setTargetAccount("target@example.com");
        transaction.setAmount(100);

        UserDataModel sourceData = new UserDataModel();
        sourceData.setUserEmail("source@example.com");
        sourceData.setBalance(1000);

        UserDataModel targetData = new UserDataModel();
        targetData.setUserEmail("target@example.com");
        targetData.setBalance(500);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail("source@example.com")).thenReturn(sourceData);
        Mockito.when(userDataRepo.findUserDataModelByUserEmail("target@example.com")).thenReturn(targetData);
        Mockito.when(accountRepo.findAllByAccountIdAndDateBetween(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(new ArrayList<>());

        transactionService.transaction(transaction);

        Assertions.assertEquals(900, sourceData.getBalance());
        Assertions.assertEquals(600, targetData.getBalance());
        Mockito.verify(userDataRepo, Mockito.times(1)).save(sourceData);
        Mockito.verify(userDataRepo, Mockito.times(1)).save(targetData);
        Mockito.verify(emailService, Mockito.times(1)).debitMail("source@example.com", "100");
        Mockito.verify(emailService, Mockito.times(1)).creditMail("target@example.com", "100");
        Mockito.verify(transactionRepo, Mockito.times(1)).save(any(TransactionModel.class));
    }

    @Test
    public void testTransactionInvalidSourceAccount() {
        TransactionModel transaction = new TransactionModel();
        transaction.setYourAccount("invalid@example.com");
        transaction.setTargetAccount("target@example.com");
        transaction.setAmount(100);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail("invalid@example.com")).thenReturn(null);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            transactionService.transaction(transaction);
        });
    }

    @Test
    public void testTransactionInvalidTargetAccount() {
        TransactionModel transaction = new TransactionModel();
        transaction.setYourAccount("source@example.com");
        transaction.setTargetAccount("invalid@example.com");
        transaction.setAmount(100);
        transaction.setCurrencyCode("USD");

        UserDataModel sourceData = new UserDataModel();
        sourceData.setUserEmail("source@example.com");
        sourceData.setBalance(1000);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail("source@example.com")).thenReturn(sourceData);
        Mockito.when(userDataRepo.findUserDataModelByUserEmail("invalid@example.com")).thenReturn(null);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            transactionService.transaction(transaction);
        });

        Mockito.verify(userDataRepo, Mockito.times(1)).findUserDataModelByUserEmail("source@example.com");
        Mockito.verify(userDataRepo, Mockito.times(1)).findUserDataModelByUserEmail("invalid@example.com");
    }

    @Test
    public void testTransactionInvalidAmount() {
        TransactionModel transaction = new TransactionModel();
        transaction.setYourAccount("source@example.com");
        transaction.setTargetAccount("target@example.com");
        transaction.setAmount(-100);
        transaction.setCurrencyCode("USD");

        UserDataModel sourceData = new UserDataModel();
        sourceData.setUserEmail("source@example.com");
        sourceData.setBalance(1000);

        UserDataModel targetData = new UserDataModel();
        targetData.setUserEmail("target@example.com");
        targetData.setBalance(500);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail("source@example.com")).thenReturn(sourceData);
        Mockito.when(userDataRepo.findUserDataModelByUserEmail("target@example.com")).thenReturn(targetData);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            transactionService.transaction(transaction);
        });

        Mockito.verify(userDataRepo, Mockito.times(1)).findUserDataModelByUserEmail("source@example.com");
        Mockito.verify(userDataRepo, Mockito.times(1)).findUserDataModelByUserEmail("target@example.com");
    }

    @Test
    public void testTransactionInsufficientBalance() {
        TransactionModel transaction = new TransactionModel();
        transaction.setYourAccount("source@example.com");
        transaction.setTargetAccount("target@example.com");
        transaction.setAmount(1000);
        UserDataModel sourceData = new UserDataModel();
        sourceData.setUserEmail("source@example.com");
        sourceData.setBalance(500);

        UserDataModel targetData = new UserDataModel();
        targetData.setUserEmail("target@example.com");
        targetData.setBalance(1000);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail("source@example.com")).thenReturn(sourceData);
        Mockito.when(userDataRepo.findUserDataModelByUserEmail("target@example.com")).thenReturn(targetData);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            transactionService.transaction(transaction);
        });


        Mockito.verify(userDataRepo, Mockito.times(1)).findUserDataModelByUserEmail("source@example.com");
        Mockito.verify(userDataRepo, Mockito.times(1)).findUserDataModelByUserEmail("target@example.com");
    }
}
