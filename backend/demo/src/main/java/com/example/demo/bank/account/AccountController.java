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

    @Operation(summary = "Get all accounts with pagination",
            description = "Retrieve a paginated list of all accounts. Default page is 0 and default size is 20.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully")
    })
    @GetMapping
    public PageResponse<AccountResponse> getAccounts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Page<AccountResponse> pageData = accountService.getAccounts(page, size);

        return new PageResponse<>(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isFirst(),
                pageData.isLast());
    }

    // TODO: is it needed or getBalance?
    @Operation(summary = "Get account by ID",
            description = "Retrieve an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{accountId}")
    public AccountResponse getAccount(
            @Parameter(name = "accountId", description = "The unique identifier of the account", required = true)
            @PathVariable("accountId") Long accountId) {

        return accountService.getAccount(accountId);
    }

    // TODO: is it needed or getAccount?
    @Operation(summary = "Get account balance",
            description = "Retrieve the balance of an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{accountId}/balance")
    public AccountResponse getBalance(
            @Parameter(name = "accountId", description = "The unique identifier of the account", required = true)
            @PathVariable("accountId") Long accountId) {

        return accountService.getAccount(accountId);
    }

    @Operation(summary = "Deposit funds into an account",
            description = "Add funds to an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds deposited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/{accountId}/deposit")
    public AccountResponse deposit(
            @Parameter(name = "accountId", description = "The unique identifier of the account", required = true)
            @PathVariable("accountId") Long accountId,
            @Valid @RequestBody DepositRequest request) {

        return accountService.deposit(accountId, request);
    }

    @Operation(summary = "Debit funds from an account",
            description = "Remove funds from an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds debited successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient funds"),
            @ApiResponse(responseCode = "400", description = "Invalid amount"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "503", description = "External logging service unavailable")
    })
    @PostMapping("/{accountId}/debit")
    public AccountResponse debit(
            @Parameter(name = "accountId", description = "The unique identifier of the account", required = true)
            @PathVariable("accountId") Long accountId,
            @Valid @RequestBody DebitRequest request) {

        return accountService.debit(accountId, request);
    }

    @Operation(summary = "Exchange currency",
            description = "Exchange currency between two accounts.")
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

    @Operation(summary = "Get transactions for an account",
            description = "Retrieve a paginated list of transactions for an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{accountId}/transactions")
    public PageResponse<TransactionResponse> getTransactions(
            @PathVariable("accountId") Long accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Page<TransactionResponse> pageData = accountService.getTransactions(
                accountId,
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
