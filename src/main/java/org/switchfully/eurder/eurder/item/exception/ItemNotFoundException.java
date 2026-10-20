package org.switchfully.eurder.eurder.item.exception;

import org.switchfully.eurder.eurder.common.exception.EurderAppException;

public class ItemNotFoundException extends EurderAppException {
    public ItemNotFoundException(String itemId) {
        super("Could not find item with id:" + itemId);
    }
}
