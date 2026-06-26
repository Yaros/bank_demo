package com.example.demo.bank.common.dto;

import java.util.List;

public record ValidationErrorResponse(
        List<String> errors) {
}
