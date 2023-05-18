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
    private String yourAccount;
    private String targetAccount;
    private Integer amount;
    private String currencyCode;
    private Date date;
}
