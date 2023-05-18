package com.example.Banking.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
@Setter
public class BalanceModel {
    @Id
    private String id;
    private Integer balance;
    private String accountId;
    private String currency;
    private String type;
    private Date date;
}
