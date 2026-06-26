package com.example.demo.bank.account;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.bank.account.dto.AccountResponse;
import com.example.demo.bank.account.dto.DebitRequest;
import com.example.demo.bank.account.dto.DepositRequest;
import com.example.demo.bank.account.dto.ExchangeRequest;
import com.example.demo.bank.common.exception.AccountNotFoundException;
import com.example.demo.bank.common.exception.GlobalExceptionHandler;
import com.example.demo.bank.transaction.dto.TransactionResponse;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void getAccounts_whenAccountsExist_returnsPagedResponse() throws Exception {
        AccountResponse response1 = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(1000.00));
        AccountResponse response2 = new AccountResponse(
                2L,
                "USD",
                BigDecimal.valueOf(2000.00));

        when(accountService.getAccounts(0, 20))
                .thenReturn(new PageImpl<>(List.of(response1, response2), PageRequest.of(0, 20), 2));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].currency").value("EUR"))
                .andExpect(jsonPath("$.content[0].balance").value(1000.00))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].currency").value("USD"))
                .andExpect(jsonPath("$.content[1].balance").value(2000.00))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void getAccount_whenAccountExists_returnsAccountResponse() throws Exception {
        AccountResponse response = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(1000.00));

        when(accountService.getAccount(1L)).thenReturn(response);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void getAccount_whenAccountMissing_returns404() throws Exception {
        when(accountService.getAccount(99L))
                .thenThrow(new AccountNotFoundException(99L));

        mockMvc.perform(get("/api/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Account not found: 99"));
    }

    @Test
    void deposit_whenAmountIsValid_returnsDepositAccountResponse() throws Exception {
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(250.00));
        AccountResponse response = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(1250.00));

        when(accountService.deposit(1L, request)).thenReturn(response);

        mockMvc.perform(post("/api/accounts/1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.balance").value(1250.00));
    }

    @Test
    void deposit_whenAmountIsNegative_returnsDepositAccountResponse() throws Exception {
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(-250.00));
        AccountResponse response = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(1250.00));

        when(accountService.deposit(1L, request)).thenReturn(response);

        mockMvc.perform(post("/api/accounts/1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("must be greater than 0"));
    }

    @Test
    void deposit_whenAmountIsMissing_returnsDepositAccountResponse() throws Exception {
        DepositRequest request = new DepositRequest(null);
        AccountResponse response = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(1000.00));

        when(accountService.deposit(1L, request)).thenReturn(response);

        mockMvc.perform(post("/api/accounts/1/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("must not be null"));
    }

    @Test
    void debit_whenAmountIsValid_returnsDebitAccountResponse() throws Exception {
        DebitRequest request = new DebitRequest(BigDecimal.valueOf(250.00));
        AccountResponse response = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(750.00));

        when(accountService.debit(1L, request)).thenReturn(response);

        mockMvc.perform(post("/api/accounts/1/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.balance").value(750.00));
    }

    @Test
    void debit_whenAmountIsNegative_returnsDebitAccountResponse() throws Exception {
        DebitRequest request = new DebitRequest(BigDecimal.valueOf(-250.00));
        AccountResponse response = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(1250.00));

        when(accountService.debit(1L, request)).thenReturn(response);

        mockMvc.perform(post("/api/accounts/1/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("must be greater than 0"));
    }

    @Test
    void debit_whenAmountIsMissing_returnsDebitAccountResponse() throws Exception {
        DebitRequest request = new DebitRequest(null);
        AccountResponse response = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(1250.00));

        when(accountService.debit(1L, request)).thenReturn(response);

        mockMvc.perform(post("/api/accounts/1/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("must not be null"));
    }

    @Test
    void exchange_whenAmountIsValid_returnsDebitAccountResponse() throws Exception {
        ExchangeRequest request = new ExchangeRequest(1L, 2L, BigDecimal.valueOf(250.00));

        doNothing().when(accountService).exchange(request);

        mockMvc.perform(post("/api/accounts/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void exchange_whenAmountIsNegative_returnsExchangeAccountResponse() throws Exception {
        ExchangeRequest request = new ExchangeRequest(1L, 2L, BigDecimal.valueOf(-250.00));

        doNothing().when(accountService).exchange(request);

        mockMvc.perform(post("/api/accounts/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("must be greater than 0"));
    }

    @Test
    void exchange_whenAmountIsMissing_returnsExchangeAccountResponse() throws Exception {
        ExchangeRequest request = new ExchangeRequest(1L, 2L, null);

        doNothing().when(accountService).exchange(request);

        mockMvc.perform(post("/api/accounts/exchange")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("must not be null"));
    }

    @Test
    void getAccount_whenAccountExists_returnsAccountBalanceResponse() throws Exception {
        AccountResponse response = new AccountResponse(
                1L,
                "EUR",
                BigDecimal.valueOf(1000.00));

        when(accountService.getAccount(1L)).thenReturn(response);

        mockMvc.perform(get("/api/accounts/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void getTransactions_whenAccountExists_returnsPagedTransactions() throws Exception {
        TransactionResponse transaction = new TransactionResponse(
                1L,
                "DEPOSIT",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(100.00),
                "ref-123",
                java.time.Instant.parse("2026-01-01T00:00:00Z"));

        when(accountService.getTransactions(1L, 0, 20))
                .thenReturn(new PageImpl<>(List.of(transaction), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/accounts/1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}
