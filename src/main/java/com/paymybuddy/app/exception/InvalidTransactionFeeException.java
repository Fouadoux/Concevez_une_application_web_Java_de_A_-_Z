package com.paymybuddy.app.exception;

public class InvalidTransactionFeeException extends RuntimeException {
    public InvalidTransactionFeeException(String message) {
        super(message);
    }
}
