package com.example.demo.bank.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.bank.common.dto.ErrorResponse;
import com.example.demo.bank.common.dto.ValidationErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAccountNotFound(
            AccountNotFoundException ex) {

        return new ErrorResponse(
                "ACCOUNT_NOT_FOUND",
                ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientFunds(
            InsufficientFundsException ex) {

        return new ErrorResponse(
                "INSUFFICIENT_FUNDS",
                ex.getMessage());
    }

    @ExceptionHandler(AmountNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAmountNotValid(
            AmountNotValidException ex) {

        return new ErrorResponse(
                "AMOUNT_NOT_VALID",
                ex.getMessage());
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleTransactionNotFound(
            TransactionNotFoundException ex) {

        return new ErrorResponse(
                "TRANSACTION_NOT_FOUND",
                ex.getMessage());
    }

    @ExceptionHandler(ExternalLoggingException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleExternalLoggingFailure(
            ExternalLoggingException ex) {

        return new ErrorResponse(
                "EXTERNAL_LOGGING_FAILURE",
                ex.getMessage());
    }

    @ExceptionHandler(ConversionRateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleConversionRateNotFound(
            ConversionRateNotFoundException ex) {

        return new ErrorResponse(
                "CONVERSION_RATE_NOT_FOUND",
                ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidation(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return new ValidationErrorResponse(errors);
    }

}
