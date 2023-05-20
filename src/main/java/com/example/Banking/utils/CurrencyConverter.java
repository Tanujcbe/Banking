package com.example.Banking.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class CurrencyConverter {
    private static final String API_ENDPOINT = "https://api.exchangeratesapi.io/latest?base=USD&symbols=INR";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static Integer convertCurrency(Integer amount, String currencyCode) {

        if(currencyCode==null || currencyCode.equals("INR")){
            return amount;
        }
        else if(currencyCode.equals("USD")){
            return amount/80;
        }
        return amount;
    }
}
