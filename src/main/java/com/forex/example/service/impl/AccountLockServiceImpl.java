package com.forex.example.service.impl;

import com.forex.example.service.AccountLockService;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountLockServiceImpl implements AccountLockService {

    private final ConcurrentHashMap<Long, Object> storage = new ConcurrentHashMap<>();

    @Override
    public Object getLockObject(Long accountId) {
        storage.putIfAbsent(accountId, new Object());
        return storage.get(accountId);
    }

    public void executeWithLock(Long accountId, Runnable execute) {
        synchronized (getLockObject(accountId)) {
            execute.run();
        }
    }

    @Override
    public void executeWithLock(Long accountOne, Long accountTwo, Runnable execute) {
        if (accountOne > accountTwo) {
            synchronized (getLockObject(accountOne)) {
                synchronized (getLockObject(accountTwo)) {
                    execute.run();
                }
            }
        } else {
            synchronized (getLockObject(accountTwo)) {
                synchronized (getLockObject(accountOne)) {
                    execute.run();
                }
            }
        }
    }

}
