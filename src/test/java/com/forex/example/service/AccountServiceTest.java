package com.forex.example.service;

import com.forex.example.BaseIntegrationTest;
import com.forex.example.controller.dto.AccountDto;
import com.forex.example.controller.dto.DepositDto;
import com.forex.example.controller.dto.MoneyTransferDto;
import com.forex.example.repository.AccountRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class AccountServiceTest extends BaseIntegrationTest {

    private static ExecutorService executorService;

    @SpyBean
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountLockService accountLockService;

    private Long accountIdOne;
    private Long accountIdTwo;

    @BeforeEach
    void init() {

        executorService = Executors.newFixedThreadPool(100);
        AccountDto accountDto = AccountDto.builder()
            .balance(1000L)
            .build();

        accountIdOne = accountService.createAccount(accountDto).getId();
        accountIdTwo = accountService.createAccount(accountDto).getId();

    }

    @Test
    void concurrentDepositTest() throws InterruptedException {

        for (int i = 0; i < 1000; i++) {
            DepositDto positive = DepositDto.builder()
                .accountId(accountIdOne)
                .deposit(1L)
                .build();

            executorService.submit(() -> accountLockService.executeWithLock(accountIdOne, () -> accountService.makeDeposit(accountIdOne, positive)));

        }

        executorService.awaitTermination(5, TimeUnit.SECONDS);
        Assertions.assertThat(accountRepository.findById(accountIdOne).orElseThrow().getBalance()).isEqualTo(2000);

    }

    @Test
    void concurrentMoneyTransferTest() throws InterruptedException {

        for (int i = 0; i < 1000; i++) {
            MoneyTransferDto moneyTransferDto1 = MoneyTransferDto.builder()
                .amount(1L)
                .toAccount(accountIdOne)
                .fromAccount(accountIdTwo)
                .build();
            MoneyTransferDto moneyTransferDto2 = MoneyTransferDto.builder()
                .amount(1L)
                .toAccount(accountIdTwo)
                .fromAccount(accountIdOne)
                .build();

            executorService.submit(() -> accountLockService.executeWithLock(accountIdOne, accountIdTwo, () -> accountService.transferMoney(moneyTransferDto1)));
            executorService.submit(() -> accountLockService.executeWithLock(accountIdOne, accountIdTwo, () -> accountService.transferMoney(moneyTransferDto2)));

        }

        executorService.awaitTermination(5, TimeUnit.SECONDS);
        Assertions.assertThat(accountRepository.findById(accountIdOne).orElseThrow().getBalance()).isEqualTo(1000);
        Assertions.assertThat(accountRepository.findById(accountIdOne).orElseThrow().getBalance()).isEqualTo(1000);

    }

}