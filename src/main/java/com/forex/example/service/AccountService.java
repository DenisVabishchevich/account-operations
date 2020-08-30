package com.forex.example.service;

import com.forex.example.controller.dto.AccountDto;
import com.forex.example.controller.dto.DepositDto;
import com.forex.example.controller.dto.MoneyTransferDto;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);

    void makeDeposit(Long accountId, DepositDto request);

    AccountDto getAccount(Long accountId);

    void transferMoney(MoneyTransferDto moneyTransferDto);
}
