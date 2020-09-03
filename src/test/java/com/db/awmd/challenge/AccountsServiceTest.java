package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalenceException;
import com.db.awmd.challenge.exception.InvalidInputException;
import com.db.awmd.challenge.exception.UserNotRegisteredException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Mock
    private NotificationService notificationService;

    @Test
    public void addAccount() throws Exception {
	Account account = new Account("Id-123");
	account.setBalance(new BigDecimal(1000));
	this.accountsService.createAccount(account);

	assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    }

    @Test
    public void addAccount_failsOnDuplicateId() throws Exception {
	String uniqueId = "Id-" + System.currentTimeMillis();
	Account account = new Account(uniqueId);
	this.accountsService.createAccount(account);

	try {
	    this.accountsService.createAccount(account);
	    fail("Should have failed when adding duplicate account");
	} catch (DuplicateAccountIdException ex) {
	    assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
	}

    }

    @Test
    public void transferAmountSuccess() throws Exception {
	accountsService.getAccountsRepository().clearAccounts();
	String uniqueId = "Id5-" + System.currentTimeMillis();
	Account account = new Account(uniqueId);
	account.setBalance(new BigDecimal(1000));
	String toUniqueId = "Id6-" + System.currentTimeMillis();
	Account account2 = new Account(toUniqueId);
	this.accountsService.createAccount(account);
	this.accountsService.createAccount(account2);

	assertThat(this.accountsService.transferAmount(toUniqueId, uniqueId, new BigDecimal("100.00")))
		.isEqualTo("amount successfully transferred,remaining balence:900.00");

    }

    @Test(expected = InsufficientBalenceException.class)
    public void transferAmountFail() throws Exception {
	accountsService.getAccountsRepository().clearAccounts();
	String uniqueId = "Id5-" + System.currentTimeMillis();
	Account account = new Account(uniqueId);
	account.setBalance(new BigDecimal(90));
	String toUniqueId = "Id6-" + System.currentTimeMillis();
	Account account2 = new Account(toUniqueId);
	this.accountsService.createAccount(account);
	this.accountsService.createAccount(account2);

	this.accountsService.transferAmount(toUniqueId, uniqueId, new BigDecimal("100.00"));

    }

    @Test(expected = InvalidInputException.class)
    public void transferAmountFailForZeroAmount() throws Exception {
	accountsService.getAccountsRepository().clearAccounts();
	String uniqueId = "Id5-" + System.currentTimeMillis();
	Account account = new Account(uniqueId);
	account.setBalance(new BigDecimal(90));
	String toUniqueId = "Id6-" + System.currentTimeMillis();
	Account account2 = new Account(toUniqueId);
	this.accountsService.createAccount(account);
	this.accountsService.createAccount(account2);

	this.accountsService.transferAmount(toUniqueId, uniqueId, new BigDecimal("0.00"));

    }

    @Test(expected = UserNotRegisteredException.class)
    public void transferAmountFailForAccountNotExist() throws Exception {
	accountsService.getAccountsRepository().clearAccounts();
	String uniqueId = "Id5-" + System.currentTimeMillis();
	Account account = new Account(uniqueId);
	account.setBalance(new BigDecimal(90));
	String toUniqueId = "Id6-" + System.currentTimeMillis();

	this.accountsService.createAccount(account);

	this.accountsService.transferAmount(toUniqueId, uniqueId, new BigDecimal("10.00"));

    }
}
