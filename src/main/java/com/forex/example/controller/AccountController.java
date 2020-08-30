package com.forex.example.controller;

import com.forex.example.controller.dto.AccountDto;
import com.forex.example.controller.dto.DepositDto;
import com.forex.example.controller.dto.MoneyTransferDto;
import com.forex.example.service.AccountLockService;
import com.forex.example.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountLockService accountLockService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create account")
    public AccountDto createAccount(@RequestBody @Valid AccountDto request) {
        return accountService.createAccount(request);
    }

    @PostMapping("{accountId}/deposit")
    @Operation(summary = "Make a deposit")
    public void makeDeposit(@PathVariable Long accountId, @RequestBody @Valid DepositDto request) {
        accountLockService.executeWithLock(accountId, () -> accountService.makeDeposit(accountId, request));
    }

    @GetMapping("{accountId}")
    @Operation(summary = "Get account by account id")
    public AccountDto getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money from one account to another")
    public void transferMoney(@RequestBody @Valid MoneyTransferDto moneyTransferDto) {
        accountLockService.executeWithLock(moneyTransferDto.getToAccount(),
            moneyTransferDto.getFromAccount(),
            () -> accountService.transferMoney(moneyTransferDto));
    }
}
