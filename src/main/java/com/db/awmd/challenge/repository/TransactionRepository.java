package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;

public interface TransactionRepository {
    void updateTransaction(Account account);
}
