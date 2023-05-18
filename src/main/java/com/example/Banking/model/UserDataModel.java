package com.example.Banking.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class UserDataModel {
    @Id
    private String userEmail;
    private String accountNumber;
    private Integer balance;
}
