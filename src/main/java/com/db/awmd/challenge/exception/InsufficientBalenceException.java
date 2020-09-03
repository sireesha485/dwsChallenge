package com.db.awmd.challenge.exception;

import java.math.BigDecimal;

public class InsufficientBalenceException extends RuntimeException {

    public InsufficientBalenceException(BigDecimal amount) {

	super(String.format("insufficient balence to transfer amount,available balence is %.2f", amount));
    }
}
