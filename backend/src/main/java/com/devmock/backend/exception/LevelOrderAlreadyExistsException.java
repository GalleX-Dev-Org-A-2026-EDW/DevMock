package com.devmock.backend.exception;

public class LevelOrderAlreadyExistsException extends RuntimeException {
    public LevelOrderAlreadyExistsException(String message) {
        super(message);
    }
}
