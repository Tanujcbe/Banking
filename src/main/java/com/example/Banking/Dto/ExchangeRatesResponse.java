package com.example.Banking.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ExchangeRatesResponse {
    @JsonProperty("rates")
    private Map<String, BigDecimal> rates;
}
