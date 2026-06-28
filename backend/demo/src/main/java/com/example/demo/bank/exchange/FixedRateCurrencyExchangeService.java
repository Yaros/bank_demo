package com.example.demo.bank.exchange;

import static java.math.RoundingMode.HALF_EVEN;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class FixedRateCurrencyExchangeService implements CurrencyExchangeService {

    private static final Map<Currency, Map<Currency, BigDecimal>> RATES = Map.of(
            Currency.getInstance("EUR"), Map.of(
                    Currency.getInstance("USD"), new BigDecimal("1.14"),
                    Currency.getInstance("GBP"), new BigDecimal("0.86"),
                    Currency.getInstance("SEK"), new BigDecimal("11.10"),
                    Currency.getInstance("VND"), new BigDecimal("29863")),
            Currency.getInstance("USD"), Map.of(
                    Currency.getInstance("EUR"), new BigDecimal("0.88"),
                    Currency.getInstance("GBP"), new BigDecimal("0.76"),
                    Currency.getInstance("SEK"), new BigDecimal("9.77"),
                    Currency.getInstance("VND"), new BigDecimal("26335")),
            Currency.getInstance("GBP"), Map.of(
                    Currency.getInstance("EUR"), new BigDecimal("1.16"),
                    Currency.getInstance("USD"), new BigDecimal("1.32"),
                    Currency.getInstance("SEK"), new BigDecimal("12.86"),
                    Currency.getInstance("VND"), new BigDecimal("34650")),
            Currency.getInstance("SEK"), Map.of(
                    Currency.getInstance("EUR"), new BigDecimal("0.090"),
                    Currency.getInstance("USD"), new BigDecimal("0.10"),
                    Currency.getInstance("GBP"), new BigDecimal("0.078"),
                    Currency.getInstance("VND"), new BigDecimal("2693.47")),
            Currency.getInstance("VND"), Map.of(
                    Currency.getInstance("EUR"), new BigDecimal("0.000033"),
                    Currency.getInstance("USD"), new BigDecimal("0.000038"),
                    Currency.getInstance("GBP"), new BigDecimal("0.000029"),
                    Currency.getInstance("SEK"), new BigDecimal("0.000370")));

    public BigDecimal convert(BigDecimal amount, Currency from, Currency to) {

        Map<Currency, BigDecimal> ratesFrom = RATES.get(from);
        BigDecimal rate = ratesFrom == null ? null : ratesFrom.get(to);
        return rate == null ? null : amount.multiply(rate).setScale(to.getDefaultFractionDigits(), HALF_EVEN);
    }
}
