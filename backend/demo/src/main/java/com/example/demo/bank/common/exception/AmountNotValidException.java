package com.example.demo.bank.common.exception;

public class AmountNotValidException extends RuntimeException {

    public AmountNotValidException(String message) {
        super(message);
    }
}
