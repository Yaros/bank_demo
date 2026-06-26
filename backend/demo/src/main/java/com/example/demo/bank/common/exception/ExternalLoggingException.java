package com.example.demo.bank.common.exception;

public class ExternalLoggingException extends RuntimeException {

    public ExternalLoggingException(String message, Throwable cause) {
        super(message, cause);
    }

}
