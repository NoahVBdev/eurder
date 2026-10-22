package org.switchfully.eurder.eurder.security.exception;

import org.switchfully.eurder.eurder.common.exception.EurderAppException;

public class ForbiddenException extends EurderAppException {
    public ForbiddenException(String message) {
        super(message);
    }
}
