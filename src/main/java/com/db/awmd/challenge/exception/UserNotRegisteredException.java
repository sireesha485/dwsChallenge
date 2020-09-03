package com.db.awmd.challenge.exception;

public class UserNotRegisteredException extends RuntimeException {

    public UserNotRegisteredException() {

	super("account not registered with bank");
    }
}
