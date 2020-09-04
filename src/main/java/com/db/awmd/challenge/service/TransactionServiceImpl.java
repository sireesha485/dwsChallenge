package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.InsufficientBalenceException;
import com.db.awmd.challenge.exception.UserNotRegisteredException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountsRepository accountsRepository;

    private ReadWriteLock accountLock = new ReentrantReadWriteLock();

    /**
     * this method allows customer to transfer funds
     * 
     * @param AccountReqDto provide account details to transfer amount
     * @return BigDecimal remaining balence
     * 
     */
    @Override
    public String transferAmount(String accountToId, String accountFromId, BigDecimal amount) {

	Account fromAccount = accountsRepository.getAccount(accountFromId);
	Account toAccount = accountsRepository.getAccount(accountToId);
	if (ObjectUtils.isEmpty(fromAccount) || ObjectUtils.isEmpty(toAccount))
	    throw new UserNotRegisteredException();
	if (fromAccount.getBalance().compareTo(amount) < 0) {
	    throw new InsufficientBalenceException(amount);
	}
	this.accountLock.writeLock().lock();
	try {
	    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
	    toAccount.setBalance(toAccount.getBalance().add(amount));
	    transactionRepository.updateTransaction(fromAccount);
	    transactionRepository.updateTransaction(toAccount);
	} finally {
	    accountLock.writeLock().unlock();
	}
	notificationService.notifyAboutTransfer(fromAccount, amount + "transferred from" + fromAccount);
	notificationService.notifyAboutTransfer(toAccount, amount + "transferred to" + toAccount);
	return "amount successfully transferred,remaining balence:" + fromAccount.getBalance();
    }
}
