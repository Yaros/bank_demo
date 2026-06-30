package com.example.demo.bank.transaction;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.bank.transaction.dto.TransactionDetailResponse;
import com.example.demo.bank.transaction.dto.TransactionResponse;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponse toResponse(TransactionEntity transaction);

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "currency", source = "account.currency")
    TransactionDetailResponse toDetailResponse(TransactionEntity transaction);
}
