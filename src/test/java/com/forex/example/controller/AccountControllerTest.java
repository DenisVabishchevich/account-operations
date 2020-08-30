package com.forex.example.controller;

import com.forex.example.BaseIntegrationTest;
import com.forex.example.controller.dto.AccountDto;
import com.forex.example.controller.dto.DepositDto;
import com.forex.example.controller.dto.MoneyTransferDto;
import com.forex.example.model.Account;
import com.forex.example.repository.AccountRepository;
import com.forex.example.service.AccountService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends BaseIntegrationTest {

    @SpyBean
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void createAccountRequest_createAccount_201Created() throws Exception {

        AccountDto accountDto = AccountDto.builder()
            .balance(0L)
            .build();

        String request = mapper.writeValueAsString(accountDto);

        mvc.perform(post("/api/v1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()));

        Mockito.verify(accountService).createAccount(accountDto);
        Assertions.assertThat(accountRepository.findAll().size()).isGreaterThan(0);

    }

    @Test
    void makeDepositRequest_increaseAccountBalance_200Ok() throws Exception {

        AccountDto accountDto = AccountDto.builder()
            .balance(0L)
            .build();

        AccountDto account = accountService.createAccount(accountDto);

        DepositDto depositDto = DepositDto.builder()
            .accountId(account.getId())
            .deposit(10L)
            .build();

        String request = mapper.writeValueAsString(depositDto);

        mvc.perform(post("/api/v1/accounts/{id}/deposit", account.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isOk());

        Mockito.verify(accountService).makeDeposit(account.getId(), depositDto);
        Assertions.assertThat(accountRepository.findById(account.getId()).orElseThrow().getBalance()).isEqualTo(10L);

    }

    @Test
    void makeDepositRequest_decreaseAccountBalance_200Ok() throws Exception {

        AccountDto accountDto = AccountDto.builder()
            .balance(10L)
            .build();

        AccountDto account = accountService.createAccount(accountDto);

        DepositDto depositDto = DepositDto.builder()
            .accountId(account.getId())
            .deposit(-10L)
            .build();

        String request = mapper.writeValueAsString(depositDto);

        mvc.perform(post("/api/v1/accounts/{id}/deposit", account.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isOk());

        Mockito.verify(accountService).makeDeposit(account.getId(), depositDto);
        Assertions.assertThat(accountRepository.findById(account.getId()).orElseThrow().getBalance()).isEqualTo(0);

    }

    @Test
    void makeDepositRequest_decreaseAccountBalance_errorNegativeBalance() throws Exception {

        AccountDto accountDto = AccountDto.builder()
            .balance(10L)
            .build();

        AccountDto account = accountService.createAccount(accountDto);

        DepositDto depositDto = DepositDto.builder()
            .accountId(account.getId())
            .deposit(-20L)
            .build();

        String request = mapper.writeValueAsString(depositDto);

        mvc.perform(post("/api/v1/accounts/{id}/deposit", account.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isBadRequest());

        Mockito.verify(accountService).makeDeposit(account.getId(), depositDto);
        Assertions.assertThat(accountRepository.findById(account.getId()).orElseThrow().getBalance()).isEqualTo(10L);

    }

    @Test
    void getAccountByIdRequest_findAccount_200OkAndReturnAccount() throws Exception {

        AccountDto accountDto = AccountDto.builder()
            .balance(10L)
            .build();

        AccountDto account = accountService.createAccount(accountDto);

        mvc.perform(get("/api/v1/accounts/{id}", account.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.balance", is(10)));

    }

    @Test
    void transferMoneyRequest_transferMoney_success200Ok() throws Exception {

        AccountDto accountDto1 = AccountDto.builder()
            .balance(10L)
            .build();

        AccountDto account1 = accountService.createAccount(accountDto1);

        AccountDto accountDto2 = AccountDto.builder()
            .balance(10L)
            .build();

        AccountDto account2 = accountService.createAccount(accountDto2);

        MoneyTransferDto moneyTransferDto = MoneyTransferDto.builder()
            .fromAccount(account1.getId())
            .toAccount(account2.getId())
            .amount(10L)
            .build();

        mvc.perform(post("/api/v1/accounts/transfer")
            .content(mapper.writeValueAsString(moneyTransferDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        Account acc2 = accountRepository.findById(account2.getId()).orElseThrow();
        Account acc1 = accountRepository.findById(account1.getId()).orElseThrow();

        Assertions.assertThat(acc2.getBalance()).isEqualTo(20L);
        Assertions.assertThat(acc1.getBalance()).isEqualTo(0L);
    }

    @Test
    void transferMoneyRequest_transferMoney_errorBadRequest() throws Exception {

        AccountDto accountDto1 = AccountDto.builder()
            .balance(10L)
            .build();

        AccountDto account1 = accountService.createAccount(accountDto1);

        AccountDto accountDto2 = AccountDto.builder()
            .balance(10L)
            .build();

        AccountDto account2 = accountService.createAccount(accountDto2);

        MoneyTransferDto moneyTransferDto = MoneyTransferDto.builder()
            .fromAccount(account1.getId())
            .toAccount(account2.getId())
            .amount(20L)
            .build();

        mvc.perform(post("/api/v1/accounts/transfer")
            .content(mapper.writeValueAsString(moneyTransferDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        Account acc2 = accountRepository.findById(account2.getId()).orElseThrow();
        Account acc1 = accountRepository.findById(account1.getId()).orElseThrow();

        Assertions.assertThat(acc2.getBalance()).isEqualTo(10L);
        Assertions.assertThat(acc1.getBalance()).isEqualTo(10L);
    }

}