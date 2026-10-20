package org.switchfully.eurder.eurder.customer.exception;

import jakarta.validation.constraints.NotBlank;
import org.switchfully.eurder.eurder.common.exception.ConflictException;

public class EmailAlreadyInUseException extends ConflictException {
    public EmailAlreadyInUseException(@NotBlank String email) {
        super("A customer with email " + email + " already exists");
    }
}
