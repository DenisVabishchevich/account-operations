package com.forex.example.service;

public interface AccountLockService {
    Object getLockObject(Long accountId);

    void executeWithLock(Long accountId, Runnable execute);

    void executeWithLock(Long accountOne, Long accountTwo, Runnable execute);
}
