package com.example.demo.bank.account;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bank.account.dto.AccountResponse;
import com.example.demo.bank.account.dto.DebitRequest;
import com.example.demo.bank.account.dto.DepositRequest;
import com.example.demo.bank.account.dto.ExchangeRequest;
import com.example.demo.bank.common.dto.PageResponse;
import com.example.demo.bank.transaction.dto.TransactionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Get all accounts with pagination", description = "Retrieve a paginated list of all accounts. Default page is 0 and default size is 20.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully")
    })
    @GetMapping
    public PageResponse<AccountResponse> getAccounts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        // Authentication is outside the scope. The API currently returns accounts for a
        // mock authenticated user. The service layer is designed to accept a user ID
        // and can be integrated with Spring Security without changes to the business
        // logic.
        long mockCurrentUser = 1L;

        Page<AccountResponse> pageData = accountService.getAccounts(mockCurrentUser, page, size);

        return new PageResponse<>(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isFirst(),
                pageData.isLast());
    }

    @Operation(summary = "Get account by ID", description = "Retrieve an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}")
    public AccountResponse getAccount(
            @Parameter(name = "id", description = "The unique identifier of the account", required = true) @PathVariable("id") Long id) {

        return accountService.getAccount(id);
    }

    @Operation(summary = "Get account balance", description = "Retrieve the balance of an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}/balance")
    public AccountResponse getBalance(
            @Parameter(name = "id", description = "The unique identifier of the account", required = true) @PathVariable("id") Long id) {

        return accountService.getAccount(id);
    }

    @Operation(summary = "Deposit funds into an account", description = "Add funds to an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds deposited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(
            @Parameter(name = "id", description = "The unique identifier of the account", required = true) @PathVariable("id") Long id,
            @Valid @RequestBody DepositRequest request) {

        return accountService.deposit(id, request);
    }

    @Operation(summary = "Debit funds from an account", description = "Remove funds from an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds debited successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient funds"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "503", description = "External logging service unavailable")
    })
    @PostMapping("/{id}/debit")
    public AccountResponse debit(
            @Parameter(name = "id", description = "The unique identifier of the account", required = true) @PathVariable("id") Long id,
            @Valid @RequestBody DebitRequest request) {

        return accountService.debit(id, request);
    }

    @Operation(summary = "Exchange currency", description = "Exchange currency between two accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency exchanged successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "404", description = "Conversion rate not found")
    })
    @PostMapping("/exchange")
    public void exchange(
            @Valid @RequestBody ExchangeRequest request) {

        accountService.exchange(request);
    }

    @Operation(summary = "Get transactions for an account", description = "Retrieve a paginated list of transactions for an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}/transactions")
    public PageResponse<TransactionResponse> getTransactions(
            @PathVariable("id") Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Page<TransactionResponse> pageData = accountService.getTransactions(
                id,
                page,
                size);

        return new PageResponse<>(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isFirst(),
                pageData.isLast());
    }
}
