package com.example.demo.bank.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        Long id,
        String type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String referenceId,
        Instant createdAt) {
}
