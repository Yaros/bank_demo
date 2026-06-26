package com.example.demo.bank.common.dto;

public record ErrorResponse(
        String code,
        String message) {
}
