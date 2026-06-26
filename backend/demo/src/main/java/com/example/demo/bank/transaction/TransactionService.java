package com.example.demo.bank.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.bank.account.AccountEntity;
import com.example.demo.bank.common.domain.TransactionType;
import com.example.demo.bank.common.exception.TransactionNotFoundException;
import com.example.demo.bank.transaction.dto.TransactionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public void createTransaction(AccountEntity account, TransactionType type, BigDecimal amount, String referenceId) {
        saveTransaction(account, type, amount, referenceId);
    }

    public Page<TransactionResponse> findByAccountId(Long accountId, int page, int size) {

        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, PageRequest.of(page, size))
                .map(transactionMapper::toResponse);
    }

    public TransactionResponse getTransaction(Long transactionId) {

        return transactionMapper.toResponse(
                transactionRepository.findById(transactionId)
                        .orElseThrow(() -> new TransactionNotFoundException(transactionId)));
    }

    public String generateReferenceId() {
        return UUID.randomUUID().toString();
    }

    private void saveTransaction(AccountEntity account, TransactionType type, BigDecimal amount, String referenceId) {

        TransactionEntity transaction = TransactionEntity.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .referenceId(referenceId)
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(transaction);
    }
}
