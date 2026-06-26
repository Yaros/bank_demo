package com.example.demo.bank.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo.bank.account.AccountEntity;
import com.example.demo.bank.common.domain.TransactionType;
import com.example.demo.bank.user.UserEntity;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldReturnTransactionsSortedByCreatedAtDesc() {
        // given
        UserEntity user = new UserEntity();
        user.setName("John Doe");
        entityManager.persist(user);

        AccountEntity account = new AccountEntity();
        account.setCurrency(Currency.getInstance("EUR"));
        account.setBalance(BigDecimal.valueOf(0));
        account.setUser(user);
        entityManager.persist(account);

        TransactionEntity t1 = new TransactionEntity();
        t1.setAccount(account);
        t1.setAmount(BigDecimal.valueOf(100));
        t1.setType(TransactionType.DEPOSIT);
        t1.setBalanceAfter(BigDecimal.valueOf(100));
        t1.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));

        TransactionEntity t2 = new TransactionEntity();
        t2.setAccount(account);
        t2.setAmount(BigDecimal.valueOf(200));
        t2.setType(TransactionType.DEPOSIT);
        t2.setBalanceAfter(BigDecimal.valueOf(300));
        t2.setCreatedAt(Instant.parse("2026-01-02T00:00:00Z"));

        entityManager.persist(t1);
        entityManager.persist(t2);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<TransactionEntity> result = transactionRepository.findByAccountIdOrderByCreatedAtDesc(
                account.getId(),
                pageable);

        // then
        List<TransactionEntity> content = result.getContent();

        assertEquals(2, content.size());

        // newest first (DESC)
        assertEquals(BigDecimal.valueOf(200), content.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(100), content.get(1).getAmount());
    }

    @Test
    void shouldReturnPaginatedResults() {
        // given
        UserEntity user = new UserEntity();
        user.setName("John Doe");
        entityManager.persist(user);

        AccountEntity account = new AccountEntity();
        account.setCurrency(Currency.getInstance("EUR"));
        account.setBalance(BigDecimal.valueOf(0));
        account.setUser(user);
        entityManager.persist(account);

        for (int i = 1; i <= 5; i++) {
            TransactionEntity tx = new TransactionEntity();
            tx.setAccount(account);
            tx.setAmount(BigDecimal.valueOf(200));
            tx.setType(TransactionType.DEPOSIT);
            tx.setBalanceAfter(BigDecimal.valueOf(i * 100));
            tx.setCreatedAt(Instant.now().plusSeconds(i));
            entityManager.persist(tx);
        }

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 2);

        // when
        Page<TransactionEntity> result = transactionRepository.findByAccountIdOrderByCreatedAtDesc(
                account.getId(),
                pageable);

        // then
        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
    }
}
