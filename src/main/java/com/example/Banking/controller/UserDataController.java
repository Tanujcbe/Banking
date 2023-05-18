package com.example.Banking.controller;

import com.example.Banking.model.BalanceModel;
import com.example.Banking.model.TransactionModel;
import com.example.Banking.service.SessionService;
import com.example.Banking.service.TransactionService;
import com.example.Banking.service.UserDataService;
import com.example.Banking.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserDataController {
    @Autowired
    UserDataService userDataService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    SessionService sessionService;
    @Autowired
    UserService userService;
    @PostMapping("/transaction")
    public ResponseEntity<String> transaction(@RequestBody TransactionModel transactionModel, HttpSession session) {
        ResponseEntity responseEntity;
        if (!sessionService.isValidSession(session.getId(),transactionModel.getYourAccount())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
        }
        try{
            transactionService.transaction(transactionModel);
            responseEntity = new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (ResponseStatusException exception){
            responseEntity = new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    @PostMapping("/deposit")
    public ResponseEntity<String> depositAmount (@RequestBody BalanceModel balanceModel,HttpSession session) {
        if (!sessionService.isValidSession(session.getId(), balanceModel.getAccountId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
        }
        ResponseEntity responseEntity;
        try{
            userDataService.depositCash(balanceModel);
            responseEntity = new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (ResponseStatusException exception){
            responseEntity = new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        userDataService.depositCash(balanceModel);
        return responseEntity;
    }
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawAmount(@RequestBody BalanceModel balanceModel,HttpSession session) {
        if (!sessionService.isValidSession(session.getId(),balanceModel.getAccountId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
        }
        ResponseEntity responseEntity;
        try{
            userDataService.withdrawCash(balanceModel);
            responseEntity = new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (ResponseStatusException exception){
            responseEntity = new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    @GetMapping("/myTransactions/{userMail}/{fromDate}/{toDate}")
    public ResponseEntity<String> pastTransaction(@PathVariable String userMail, HttpSession session, @PathVariable String fromDate, @PathVariable String toDate) {
        ResponseEntity responseEntity;
        if (!sessionService.isValidSession(session.getId(),userMail) && !sessionService.isAdmin(session.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
        }
        try{
            List<TransactionModel> transactions=transactionService.pastTransactions(userMail, convertStringToStartDate(fromDate), convertStringToEndDate(toDate));
            responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (ResponseStatusException exception){
            responseEntity = new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ParseException parseException) {
            responseEntity =new ResponseEntity<>("Invalid Date", HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    @GetMapping("/myOffilineTransactions/{userMail}/{fromDate}/{toDate}")
    public ResponseEntity<String> pastOfflineTransaction(@PathVariable String userMail, HttpSession session, @PathVariable String fromDate, @PathVariable String toDate) {
        ResponseEntity responseEntity;
        if (!sessionService.isValidSession(session.getId(),userMail) && !sessionService.isAdmin(session.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
        }
        try{
            List<BalanceModel> transactions=transactionService.pastOfflineTransactions(userMail, convertStringToStartDate(fromDate), convertStringToEndDate(toDate));
            responseEntity = new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (ResponseStatusException exception){
            responseEntity = new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ParseException parseException) {
            responseEntity =new ResponseEntity<>("Invalid Date", HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    @GetMapping("/checkBalance/{userMail}")
    public ResponseEntity<String> getBalance(@PathVariable String userMail, HttpSession session) {
        ResponseEntity responseEntity;
        if (!sessionService.isValidSession(session.getId(),userMail) && !sessionService.isAdmin(session.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session");
        }
        try{
            Integer balance=userDataService.checkBalance(userMail);
            responseEntity = new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (ResponseStatusException exception){
            responseEntity = new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }
    public static Date convertStringToStartDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(dateString);
    }
    public static Date convertStringToEndDate(String dateString) throws ParseException {
        LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.of(23, 59, 59));
        return Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}

