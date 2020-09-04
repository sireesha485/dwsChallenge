package com.db.awmd.challenge.repository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.utility.AccountUtilityBean;

@Repository
public class TransactionRepositoryInMemory implements TransactionRepository {
    @Autowired
    AccountUtilityBean utility;

    @Override
    public void updateTransaction(Account account) {
	Map<String, Account> accounts = utility.getAccounts();
	accounts.put(account.getAccountId(), account);
    }
}
