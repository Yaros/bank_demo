package com.example.demo.bank.transaction;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.bank.common.exception.GlobalExceptionHandler;
import com.example.demo.bank.common.exception.TransactionNotFoundException;
import com.example.demo.bank.transaction.dto.TransactionResponse;

@WebMvcTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void getTransaction_whenTransactionExists_returnsTransactionResponse() throws Exception {
        TransactionResponse response = new TransactionResponse(
                1L,
                "DEPOSIT",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(100.00),
                "ref-123",
                Instant.parse("2026-01-01T00:00:00Z"));

        when(transactionService.getTransaction(1L)).thenReturn(response);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.balanceAfter").value(100.00))
                .andExpect(jsonPath("$.referenceId").value("ref-123"))
                .andExpect(jsonPath("$.createdAt").value("2026-01-01T00:00:00Z"));
    }

    @Test
    void getTransaction_whenTransactionNotFound_returns404() throws Exception {
        when(transactionService.getTransaction(99L))
                .thenThrow(new TransactionNotFoundException(99L));

        mockMvc.perform(get("/api/transactions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TRANSACTION_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Transaction not found: 99"));
    }
}
