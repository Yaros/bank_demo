package com.example.demo.bank.common.exception;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(Long transactionId) {
        super("Transaction not found: " + transactionId);
    }
}
