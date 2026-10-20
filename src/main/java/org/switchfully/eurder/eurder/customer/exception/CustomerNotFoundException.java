package org.switchfully.eurder.eurder.customer.exception;

import org.switchfully.eurder.eurder.common.exception.EurderAppException;

public class CustomerNotFoundException extends EurderAppException {
    public CustomerNotFoundException(String id) {
        super("Could not find a customer with id: " + id);
    }
}
