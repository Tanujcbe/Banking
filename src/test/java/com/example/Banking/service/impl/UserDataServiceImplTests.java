package com.example.Banking.service.impl;

import com.example.Banking.model.BalanceModel;
import com.example.Banking.model.UserDataModel;
import com.example.Banking.model.UserModel;
import com.example.Banking.repo.AccountRepo;
import com.example.Banking.repo.UserDataRepo;
import com.example.Banking.service.EmailService;
import com.example.Banking.service.impl.UserDataServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

public class UserDataServiceImplTests {

    @Mock
    private UserDataRepo userDataRepo;

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserDataServiceImpl userDataService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBankDetails() {
        UserModel userModel = new UserModel();
        userModel.setUserEmail("test@example.com");

        userDataService.createBankDetails(userModel);

        Mockito.verify(userDataRepo, Mockito.times(1)).insert(Mockito.any(UserDataModel.class));
    }

    @Test
    public void testDepositCash() {
        BalanceModel balanceModel = new BalanceModel();
        balanceModel.setAccountId("test@example.com");
        balanceModel.setBalance(100);

        UserDataModel userData = new UserDataModel();
        userData.setUserEmail("test@example.com");
        userData.setBalance(50);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail(Mockito.anyString())).thenReturn(userData);

        userDataService.depositCash(balanceModel);

        Mockito.verify(userDataRepo, Mockito.times(1)).save(Mockito.any(UserDataModel.class));
        Mockito.verify(accountRepo, Mockito.times(1)).save(Mockito.any(BalanceModel.class));
        Mockito.verify(emailService, Mockito.times(1)).creditMail(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testWithdrawCashSufficientBalance() {
        BalanceModel balanceModel = new BalanceModel();
        balanceModel.setAccountId("test@example.com");
        balanceModel.setBalance(500);

        UserDataModel userData = new UserDataModel();
        userData.setUserEmail("test@example.com");
        userData.setBalance(100000);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail(Mockito.anyString())).thenReturn(userData);

        userDataService.withdrawCash(balanceModel);

        Mockito.verify(userDataRepo, Mockito.times(1)).save(Mockito.any(UserDataModel.class));
        Mockito.verify(accountRepo, Mockito.times(1)).save(Mockito.any(BalanceModel.class));
        Mockito.verify(emailService, Mockito.times(1)).debitMail(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testWithdrawCashInsufficientBalance() {
        BalanceModel balanceModel = new BalanceModel();
        balanceModel.setAccountId("test@example.com");
        balanceModel.setBalance(100);

        UserDataModel userData = new UserDataModel();
        userData.setUserEmail("test@example.com");
        userData.setBalance(50);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail(Mockito.anyString())).thenReturn(userData);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            userDataService.withdrawCash(balanceModel);
        });
    }

    @Test
    public void testCheckBalanceAccountNotFound() {
        String userMail = "test@example.com";

        Mockito.when(userDataRepo.findUserDataModelByUserEmail(Mockito.anyString())).thenReturn(null);

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            userDataService.checkBalance(userMail);
        });
    }

    @Test
    public void testCheckBalance() {
        String userMail = "test@example.com";

        UserDataModel userData = new UserDataModel();
        userData.setUserEmail(userMail);
        userData.setBalance(100);

        Mockito.when(userDataRepo.findUserDataModelByUserEmail(Mockito.anyString())).thenReturn(userData);

        int balance = userDataService.checkBalance(userMail);

        Assertions.assertEquals(100, balance);
    }
}
