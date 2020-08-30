package com.forex.example.service.impl;

import com.forex.example.controller.dto.AccountDto;
import com.forex.example.controller.dto.DepositDto;
import com.forex.example.controller.dto.MoneyTransferDto;
import com.forex.example.controller.dto.mapper.AccountMapper;
import com.forex.example.exception.NegativeBalanceException;
import com.forex.example.model.Account;
import com.forex.example.repository.AccountRepository;
import com.forex.example.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = accountMapper.fromDto(accountDto);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public void makeDeposit(Long accountId, DepositDto request) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Cant find account id: " + accountId));
        long newBalance = account.getBalance() + request.getDeposit();
        validateBalance(newBalance);
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @Override
    public AccountDto getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new EntityNotFoundException("Cant find account id: " + accountId));
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void transferMoney(MoneyTransferDto moneyTransferDto) {

        Account accountFrom = accountRepository.findById(moneyTransferDto.getFromAccount())
            .orElseThrow(() -> new EntityNotFoundException("Cant find account id: " + moneyTransferDto.getFromAccount()));
        Account accountTo = accountRepository.findById(moneyTransferDto.getToAccount())
            .orElseThrow(() -> new EntityNotFoundException("Cant find account id: " + moneyTransferDto.getToAccount()));

        long accountFromBalance = accountFrom.getBalance() - moneyTransferDto.getAmount();
        long accountToBalance = accountTo.getBalance() + moneyTransferDto.getAmount();

        validateBalance(accountFromBalance);
        validateBalance(moneyTransferDto.getToAccount());

        accountFrom.setBalance(accountFromBalance);
        accountTo.setBalance(accountToBalance);

        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
    }

    private void validateBalance(long newBalance) {
        if (newBalance < 0) {
            throw new NegativeBalanceException("Balance cant be negative");
        }
    }
}
