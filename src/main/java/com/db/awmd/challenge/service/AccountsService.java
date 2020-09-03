package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.InsufficientBalenceException;
import com.db.awmd.challenge.exception.InvalidInputException;
import com.db.awmd.challenge.exception.UserNotRegisteredException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

@Service
public class AccountsService {
    @Getter
    private final NotificationService notificationService;
    @Getter
    private final AccountsRepository accountsRepository;

    private ReadWriteLock accountLock;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository, EmailNotificationService notificationService) {
	this.accountsRepository = accountsRepository;
	this.notificationService = notificationService;
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

    // @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String transferAmount(String accountToId, String accountFromId, BigDecimal amount) {
	if (amount.compareTo(BigDecimal.ONE) < 0)
	    throw new InvalidInputException("please enter transfer amount grater than 1");
	Account fromAccount = this.accountsRepository.getAccount(accountFromId);
	Account toAccount = this.accountsRepository.getAccount(accountToId);
	if (ObjectUtils.isEmpty(fromAccount) || ObjectUtils.isEmpty(toAccount))
	    throw new UserNotRegisteredException();
	if (fromAccount.getBalance().compareTo(amount) < 0) {
	    throw new InsufficientBalenceException(amount);
	}
	this.accountLock.writeLock().lock();
	try {
	    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
	    toAccount.setBalance(toAccount.getBalance().add(amount));
	    this.accountsRepository.updateAccount(fromAccount);
	    this.accountsRepository.updateAccount(toAccount);
	} finally {
	    this.accountLock.writeLock().unlock();
	}
	notificationService.notifyAboutTransfer(fromAccount, amount + "transferred from" + fromAccount);
	notificationService.notifyAboutTransfer(toAccount, amount + "transferred to" + toAccount);
	return "amount successfully transferred,remaining balence:" + fromAccount.getBalance();
    }
}
