package com.db.awmd.challenge.service;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;

    private ReadWriteLock accountLock;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository) {
	this.accountsRepository = accountsRepository;
	this.accountLock = new ReentrantReadWriteLock();
    }

    public void createAccount(Account account) {
	this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
	this.accountLock.readLock().lock();
	Account account = this.accountsRepository.getAccount(accountId);

	this.accountLock.readLock().unlock();
	return account;
    }
}
