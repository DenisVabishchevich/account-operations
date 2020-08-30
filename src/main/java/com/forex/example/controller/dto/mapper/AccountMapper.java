package com.forex.example.controller.dto.mapper;

import com.forex.example.controller.dto.AccountDto;
import com.forex.example.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public Account fromDto(AccountDto accountDto) {
        return Account.builder()
            .id(accountDto.getId())
            .balance(accountDto.getBalance())
            .build();
    }

    public AccountDto toDto(Account savedAccount) {
        return AccountDto.builder()
            .id(savedAccount.getId())
            .balance(savedAccount.getBalance())
            .build();
    }

}
