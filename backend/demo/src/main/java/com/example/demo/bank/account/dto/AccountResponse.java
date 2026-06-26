package com.example.demo.bank.account.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String currency,
        BigDecimal balance) {
}
