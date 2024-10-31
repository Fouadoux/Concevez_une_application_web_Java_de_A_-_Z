package com.paymybuddy.app.exception;

public class EntityDeleteException extends RuntimeException {
    public EntityDeleteException(String message, Throwable throwable) {
        super(message,throwable);
    }
}
