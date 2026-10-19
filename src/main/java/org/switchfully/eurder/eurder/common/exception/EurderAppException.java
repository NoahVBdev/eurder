package org.switchfully.eurder.eurder.common.exception;

public abstract class EurderAppException extends RuntimeException {
    protected EurderAppException(String message) {
        super(message);
    }
}
