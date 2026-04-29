package com.firstaidkit.domain.exception;

import lombok.Getter;

@Getter
public class AccountLockedException extends RuntimeException {

    private final long minutesLeft;

    public AccountLockedException(String message, long minutesLeft) {
        super(message);
        this.minutesLeft = minutesLeft;
    }
}
