package com.example.demo.bank.common.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long accountId) {
        super("Insufficient funds on account " + accountId);
    }
}
