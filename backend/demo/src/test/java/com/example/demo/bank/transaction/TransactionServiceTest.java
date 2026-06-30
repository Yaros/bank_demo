package com.example.demo.bank.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.demo.bank.account.AccountEntity;
import com.example.demo.bank.common.domain.TransactionType;
import com.example.demo.bank.common.exception.TransactionNotFoundException;
import com.example.demo.bank.transaction.dto.TransactionDetailResponse;
import com.example.demo.bank.transaction.dto.TransactionResponse;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void findByAccountId_returnsMappedPage() {
        // Given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .currency(Currency.getInstance("EUR"))
                .balance(BigDecimal.valueOf(100.00))
                .build();

        TransactionEntity transaction = TransactionEntity.builder()
                .id(1L)
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100.00))
                .balanceAfter(BigDecimal.valueOf(100.00))
                .referenceId("ref-123")
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();

        TransactionResponse expected = new TransactionResponse(
                1L,
                "DEPOSIT",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(100.00),
                "ref-123",
                Instant.parse("2026-01-01T00:00:00Z"));

        Page<TransactionEntity> page = new PageImpl<>(List.of(transaction));

        when(transactionRepository.findByAccountIdOrderByCreatedAtDesc(1L, PageRequest.of(0, 10)))
                .thenReturn(page);
        when(transactionMapper.toResponse(transaction)).thenReturn(expected);

        // When
        Page<TransactionResponse> actual = transactionService.findByAccountId(1L, 0, 10);

        // Then
        assertThat(actual.getTotalElements()).isEqualTo(1);
        assertThat(actual.getContent()).containsExactly(expected);
        verify(transactionRepository).findByAccountIdOrderByCreatedAtDesc(1L, PageRequest.of(0, 10));
        verify(transactionMapper).toResponse(transaction);
    }

    @Test
    void getTransactionDetail_whenTransactionExists_returnsResponse() {
        // Given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .currency(Currency.getInstance("EUR"))
                .balance(BigDecimal.valueOf(100.00))
                .build();

        TransactionEntity transaction = TransactionEntity.builder()
                .id(1L)
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100.00))
                .balanceAfter(BigDecimal.valueOf(100.00))
                .referenceId("ref-123")
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();

        TransactionDetailResponse expected = new TransactionDetailResponse(
                1L,
                "DEPOSIT",
                BigDecimal.valueOf(100.00),
                BigDecimal.valueOf(100.00),
                "ref-123",
                Instant.parse("2026-01-01T00:00:00Z"),
                1L,
                "EUR");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toDetailResponse(transaction)).thenReturn(expected);

        // When
        TransactionDetailResponse actual = transactionService.getTransactionDetail(1L);

        // Then
        assertThat(actual).isEqualTo(expected);
        verify(transactionRepository).findById(1L);
        verify(transactionMapper).toDetailResponse(transaction);
    }

    @Test
    void getTransactionDetail_whenTransactionMissing_throwsTransactionNotFoundException() {
        // Given
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactionDetail(99L))
                .isInstanceOf(TransactionNotFoundException.class)
                .hasMessageContaining("Transaction not found: 99");

        verify(transactionRepository).findById(99L);
    }

    @Test
    void createTransaction_savesTransactionWithCurrentAccountBalance() {
        // Given
        AccountEntity account = AccountEntity.builder()
                .id(1L)
                .currency(Currency.getInstance("EUR"))
                .balance(BigDecimal.valueOf(250.00))
                .build();

        // When
        transactionService.createTransaction(account, TransactionType.DEPOSIT, BigDecimal.valueOf(250.00), "ref-456");

        // Then
        ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionRepository).save(captor.capture());

        TransactionEntity saved = captor.getValue();
        assertThat(saved.getAccount()).isEqualTo(account);
        assertThat(saved.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(saved.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(250.00));
        assertThat(saved.getBalanceAfter()).isEqualByComparingTo(BigDecimal.valueOf(250.00));
        assertThat(saved.getReferenceId()).isEqualTo("ref-456");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void generateReferenceId_returnsValidUuid() {
        // When
        String referenceId = transactionService.generateReferenceId();

        // Then
        assertThat(UUID.fromString(referenceId)).isNotNull();
        assertThat(referenceId).isNotBlank();
    }
}
