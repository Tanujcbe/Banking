package com.example.Banking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserModel {
    public String userEmail;
    public String password;
    public String role;
}
