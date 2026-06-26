package com.example.demo.bank.account;

import org.mapstruct.Mapper;

import com.example.demo.bank.account.dto.AccountResponse;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountResponse toResponse(AccountEntity account);
}
