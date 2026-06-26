package com.example.demo.bank.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.example.demo.bank.account.dto.AccountResponse;
import com.example.demo.bank.account.dto.DebitRequest;
import com.example.demo.bank.account.dto.DepositRequest;
import com.example.demo.bank.account.dto.ExchangeRequest;
import com.example.demo.bank.common.domain.TransactionType;
import com.example.demo.bank.common.exception.AccountNotFoundException;
import com.example.demo.bank.common.exception.AmountNotValidException;
import com.example.demo.bank.common.exception.ConversionRateNotFoundException;
import com.example.demo.bank.common.exception.ExternalLoggingException;
import com.example.demo.bank.common.exception.InsufficientFundsException;
import com.example.demo.bank.exchange.CurrencyExchangeService;
import com.example.demo.bank.external.ExternalLoggingService;
import com.example.demo.bank.transaction.TransactionService;
import com.example.demo.bank.transaction.dto.TransactionResponse;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionService transactionService;

    @Mock
    private CurrencyExchangeService currencyExchangeService;

    @Mock
    private ExternalLoggingService externalLoggingService;

    @InjectMocks
    private AccountService accountService;

    private AccountEntity account;

    @BeforeEach
    void setUp() {
        account = AccountEntity.builder()
                .id(1L)
                .currency(Currency.getInstance("EUR"))
                .balance(BigDecimal.valueOf(1000.00))
                .build();
    }

    @Test
    void getAccounts_whenAccountExists_returnsMappedResponse() {
        // Given
        AccountEntity second = AccountEntity.builder()
                .id(2L)
                .currency(Currency.getInstance("USD"))
                .balance(BigDecimal.valueOf(200.00))
                .build();

        AccountResponse r1 = new AccountResponse(1L, "EUR", BigDecimal.valueOf(1000.00));
        AccountResponse r2 = new AccountResponse(2L, "USD", BigDecimal.valueOf(200.00));

        Page<AccountEntity> entities = new PageImpl<>(List.of(account, second));

        when(accountRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 10)))
                .thenReturn(entities);

        when(accountMapper.toResponse(account)).thenReturn(r1);
        when(accountMapper.toResponse(second)).thenReturn(r2);

        // When
        Page<AccountResponse> result = accountService.getAccounts(0, 10);

        // Then
        assertThat(result.getContent()).containsExactly(r1, r2);

        verify(accountRepository).findAll(org.springframework.data.domain.PageRequest.of(0, 10));
        verify(accountMapper).toResponse(account);
        verify(accountMapper).toResponse(second);
    }

    @Test
    void getAccount_whenAccountExists_returnsMappedResponse() {
        // Given
        AccountResponse expected = new AccountResponse(1L, "EUR", BigDecimal.valueOf(1000.00));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toResponse(account)).thenReturn(expected);

        // When
        AccountResponse actual = accountService.getAccount(1L);

        // Then
        assertThat(actual).isEqualTo(expected);
        verify(accountRepository).findById(1L);
        verify(accountMapper).toResponse(account);
    }

    @Test
    void getAccount_whenAccountMissing_throwsAccountNotFoundException() {
        // Given
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.getAccount(2L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account not found: 2");
    }

    @Test
    void deposit_whenAmountIsValid_updatesBalanceAndCreatesTransaction() {
        // Given
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(250.00));
        BigDecimal expectedBalance = BigDecimal.valueOf(1250.00);
        AccountResponse expected = new AccountResponse(1L, "EUR", expectedBalance);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toResponse(account)).thenReturn(expected);

        // When
        AccountResponse response = accountService.deposit(1L, request);

        // Then
        assertThat(response).isEqualTo(expected);
        assertThat(response.balance()).isEqualByComparingTo(expectedBalance);
        assertThat(account.getBalance()).isEqualByComparingTo(expectedBalance);
        verify(accountRepository).save(account);
        verify(transactionService).createTransaction(account, TransactionType.DEPOSIT, request.amount(), null);
    }

    @Test
    void debit_whenInsufficientFunds_throwsInsufficientFundsException() {
        // Given
        account.setBalance(BigDecimal.valueOf(100.00));
        DebitRequest request = new DebitRequest(BigDecimal.valueOf(150.00));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // When & Then
        assertThatThrownBy(() -> accountService.debit(1L, request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Insufficient funds on account 1");

        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
    }

    @Test
    void debit_whenSuccessful_decreasesBalanceAndCreatesTransaction() {
        // Given
        DebitRequest request = new DebitRequest(BigDecimal.valueOf(200.00));
        BigDecimal expectedBalance = BigDecimal.valueOf(800.00);
        AccountResponse expected = new AccountResponse(1L, "EUR", expectedBalance);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toResponse(account)).thenReturn(expected);

        // When
        AccountResponse response = accountService.debit(1L, request);

        // Then
        assertThat(response).isEqualTo(expected);
        assertThat(response.balance()).isEqualByComparingTo(expectedBalance);
        assertThat(account.getBalance()).isEqualByComparingTo(expectedBalance);
        verify(externalLoggingService).logDebit();
        verify(accountRepository).save(account);
        verify(transactionService).createTransaction(account, TransactionType.DEBIT, request.amount(), null);
    }

    @Test
    void debit_whenExternalLoggingFails_throwsExternalLoggingException() {
        // Given
        DebitRequest request = new DebitRequest(BigDecimal.valueOf(100.00));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        doThrow(new RuntimeException("downstream failure"))
                .when(externalLoggingService).logDebit();

        // When & Then
        assertThatThrownBy(() -> accountService.debit(1L, request))
                .isInstanceOf(ExternalLoggingException.class)
                .hasMessageContaining("Failed to log debit operation for account 1");

        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
    }

    @Test
    void debit_whenAmountHasTooManyFractionDigits_throwsAmountNotValidException() {
        // Given
        DebitRequest request = new DebitRequest(BigDecimal.valueOf(10.123));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // When & Then
        assertThatThrownBy(() -> accountService.debit(1L, request))
                .isInstanceOf(AmountNotValidException.class)
                .hasMessageContaining("Too many fractional digits for currency EUR");

        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
    }

    @Test
    void exchange_whenSourceHasInsufficientFunds_throwsInsufficientFundsException() {
        // Given
        AccountEntity target = AccountEntity.builder()
                .id(2L)
                .currency(Currency.getInstance("USD"))
                .balance(BigDecimal.valueOf(200.00))
                .build();

        account.setBalance(BigDecimal.valueOf(50.00));
        ExchangeRequest request = new ExchangeRequest(1L, 2L, BigDecimal.valueOf(100.00));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(target));

        // When & Then
        assertThatThrownBy(() -> accountService.exchange(request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Insufficient funds on account 1");

        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
    }

    @Test
    void exchange_whenConversionRateNotFound_throwsConversionRateNotFoundException() {
        // Given
        AccountEntity target = AccountEntity.builder()
                .id(2L)
                .currency(Currency.getInstance("USD"))
                .balance(BigDecimal.valueOf(200.00))
                .build();

        ExchangeRequest request = new ExchangeRequest(1L, 2L, BigDecimal.valueOf(100.00));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(target));
        when(currencyExchangeService.convert(request.amount(), account.getCurrency(), target.getCurrency()))
                .thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> accountService.exchange(request))
                .isInstanceOf(ConversionRateNotFoundException.class)
                .hasMessageContaining("Conversion rate not found for currencies");

        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
    }

    @Test
    void exchange_whenSuccessful_exchangesBalancesAndCreatesTransactions() {
        // Given
        AccountEntity target = AccountEntity.builder()
                .id(2L)
                .currency(Currency.getInstance("USD"))
                .balance(BigDecimal.valueOf(200.00))
                .build();

        ExchangeRequest request = new ExchangeRequest(1L, 2L, BigDecimal.valueOf(100.00));
        BigDecimal convertedAmount = BigDecimal.valueOf(90.00);
        String referenceId = "ref-12345";

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(target));
        when(currencyExchangeService.convert(request.amount(), account.getCurrency(), target.getCurrency()))
                .thenReturn(convertedAmount);
        when(transactionService.generateReferenceId()).thenReturn(referenceId);

        // When
        accountService.exchange(request);

        // Then
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(900.00));
        assertThat(target.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(290.00));

        verify(accountRepository).save(account);
        verify(accountRepository).save(target);
        verify(transactionService).createTransaction(account, TransactionType.EXCHANGE_OUT, request.amount(),
                referenceId);
        verify(transactionService).createTransaction(target, TransactionType.EXCHANGE_IN, convertedAmount, referenceId);
    }

    @Test
    void getTransactions_delegatesToTransactionService() {
        // Given
        TransactionResponse response = new TransactionResponse(
                1L,
                "DEPOSIT",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(100.00),
                UUID.randomUUID().toString(),
                Instant.now());
        Page<TransactionResponse> page = new PageImpl<>(List.of(response));

        when(transactionService.findByAccountId(1L, 0, 10)).thenReturn(page);

        // When
        Page<TransactionResponse> actual = accountService.getTransactions(1L, 0, 10);

        // Then
        assertThat(actual).isEqualTo(page);
        assertThat(actual.getContent()).containsExactly(response);
        verify(transactionService).findByAccountId(1L, 0, 10);
    }
}
