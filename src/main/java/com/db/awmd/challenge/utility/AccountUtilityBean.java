package com.db.awmd.challenge.utility;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;

@Component
public class AccountUtilityBean {

    @Autowired
    public AccountsRepository accountsRepositoryInMemory;

    public Map<String, Account> getAccounts() {
	return accountsRepositoryInMemory.getAccounts();

    }

}
