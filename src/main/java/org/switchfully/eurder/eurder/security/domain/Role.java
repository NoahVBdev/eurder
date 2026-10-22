package org.switchfully.eurder.eurder.security.domain;

import java.util.EnumSet;
import java.util.Set;

import static org.switchfully.eurder.eurder.security.domain.Feature.*;

public enum Role {
    CUSTOMER(EnumSet.of(
            PLACE_ORDER,
            REORDER,
            VIEW_ORDER_REPORT
    )),
    ADMIN(EnumSet.of(
            VIEW_ALL_CUSTOMERS,
            VIEW_CUSTOMER,
            ADD_ITEM,
            UPDATE_ITEM,
            VIEW_SHIPPING_TODAY,
            VIEW_ITEM_OVERVIEW
    ));

    private final Set<Feature> features;

    Role(Set<Feature> features) {
        this.features = features;
    }

    public boolean hasFeature(Feature feature) {
        return features.contains(feature);
    }
}
