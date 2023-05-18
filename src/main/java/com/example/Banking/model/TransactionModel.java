package com.example.Banking.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
@Setter
public class TransactionModel {
    @Id
    private String id;
    String yourAccount;
    String targetAccount;
    Integer amount;
    String currencyCode;
    private Date date;
}
