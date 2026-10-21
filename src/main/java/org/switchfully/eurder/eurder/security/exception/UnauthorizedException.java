package org.switchfully.eurder.eurder.security.exception;

import org.switchfully.eurder.eurder.common.exception.EurderAppException;

public class UnauthorizedException extends EurderAppException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
