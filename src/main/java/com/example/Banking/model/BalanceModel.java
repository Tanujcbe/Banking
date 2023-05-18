package com.example.Banking.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
@Setter
public class BalanceModel {
    @Id
    String id;
    Integer balance;
    String accountId;
    String currency;
    String type;
    Date date;
}
