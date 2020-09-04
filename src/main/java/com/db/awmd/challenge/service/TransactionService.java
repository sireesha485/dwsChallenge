package com.db.awmd.challenge.service;

import java.math.BigDecimal;

public interface TransactionService {
    public String transferAmount(String accountToId, String accountFromId, BigDecimal amount);

}
