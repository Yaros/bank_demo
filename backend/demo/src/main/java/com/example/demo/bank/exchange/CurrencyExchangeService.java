package com.example.demo.bank.exchange;

import java.math.BigDecimal;
import java.util.Currency;

public interface CurrencyExchangeService {
    BigDecimal convert(BigDecimal amount, Currency from, Currency to);
}
