package com.example.demo.bank.account;

import java.math.BigDecimal;
import java.util.Currency;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionService transactionService;
    private final CurrencyExchangeService currencyExchangeService;
    private final ExternalLoggingService externalLoggingService;

    public Page<AccountResponse> getAccounts(int page, int size) {
        return accountRepository.findAll(PageRequest.of(page, size)).map(accountMapper::toResponse);
    }

    public AccountResponse getAccount(Long accountId) {
        return accountMapper.toResponse(getAccountEntity(accountId));
    }

    @Transactional
    public AccountResponse deposit(Long accountId, DepositRequest request) {
        AccountEntity account = getAccountEntity(accountId);

        validateAmount(request.amount(), account.getCurrency());

        account.setBalance(account.getBalance().add(request.amount()));

        accountRepository.save(account);

        transactionService.createTransaction(
                account,
                TransactionType.DEPOSIT,
                request.amount(),
                null);

        return accountMapper.toResponse(account);
    }

    @Transactional
    public AccountResponse debit(Long accountId, DebitRequest request) {
        AccountEntity account = getAccountEntity(accountId);

        validateAmount(request.amount(), account.getCurrency());

        try {
            externalLoggingService.logDebit();
        } catch (Exception e) {
            throw new ExternalLoggingException("Failed to log debit operation for account " + accountId, e);
        }

        if (account.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(accountId);
        }

        account.setBalance(account.getBalance().subtract(request.amount()));

        accountRepository.save(account);

        transactionService.createTransaction(
                account,
                TransactionType.DEBIT,
                request.amount(),
                null);

        return accountMapper.toResponse(account);
    }

    @Transactional
    public void exchange(ExchangeRequest request) {
        AccountEntity source = getAccountEntity(request.fromAccountId());
        AccountEntity target = getAccountEntity(request.toAccountId());

        validateAmount(request.amount(), source.getCurrency());

        if (source.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException(source.getId());
        }

        BigDecimal convertedAmount = currencyExchangeService.convert(
                request.amount(),
                source.getCurrency(),
                target.getCurrency());

        if (convertedAmount == null) {
            throw new ConversionRateNotFoundException("Conversion rate not found for currencies " + source.getCurrency()
                    + " and " + target.getCurrency());
        }

        source.setBalance(source.getBalance().subtract(request.amount()));
        target.setBalance(target.getBalance().add(convertedAmount));

        accountRepository.save(source);
        accountRepository.save(target);

        String referenceId = transactionService.generateReferenceId();

        transactionService.createTransaction(
                source,
                TransactionType.EXCHANGE_OUT,
                request.amount(),
                referenceId);

        transactionService.createTransaction(
                target,
                TransactionType.EXCHANGE_IN,
                convertedAmount,
                referenceId);
    }

    public Page<TransactionResponse> getTransactions(
            Long accountId,
            int page,
            int size) {

        return transactionService.findByAccountId(
                accountId,
                page,
                size);
    }

    private AccountEntity getAccountEntity(Long accountId) {

        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private void validateAmount(BigDecimal amount, Currency currency) {

        int scale = currency.getDefaultFractionDigits();

        if (amount.scale() > scale) {
            throw new AmountNotValidException("Too many fractional digits for currency " + currency.getCurrencyCode()
                    + ". Expected scale: " + scale + ", but got: " + amount.scale());
        }
    }
}
