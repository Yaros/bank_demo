package com.example.demo.bank.transaction;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.bank.transaction.dto.TransactionResponse;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "type", expression = "java(transaction.getType().name())")
    TransactionResponse toResponse(TransactionEntity transaction);
}
