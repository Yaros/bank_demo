package com.example.demo.bank.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Page<AccountEntity> findByUserId(Long userId, PageRequest pageRequest);
}
