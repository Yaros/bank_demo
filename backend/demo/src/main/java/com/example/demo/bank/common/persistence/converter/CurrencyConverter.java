package com.example.demo.bank.common.persistence.converter;

import java.util.Currency;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CurrencyConverter
        implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(
            Currency currency) {

        return currency == null
                ? null
                : currency.getCurrencyCode();
    }

    @Override
    public Currency convertToEntityAttribute(
            String currencyCode) {

        return currencyCode == null
                ? null
                : Currency.getInstance(currencyCode);
    }
}
