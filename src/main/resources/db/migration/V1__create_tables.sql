SET search_path TO eurder;

CREATE TABLE customer
(
    id            UUID         NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone_number  VARCHAR(30)  NOT NULL,
    street        VARCHAR(100) NOT NULL,
    house_number  VARCHAR(20)  NOT NULL,
    postal_code   VARCHAR(20)  NOT NULL,
    city          VARCHAR(100) NOT NULL,
    country       VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    CONSTRAINT pk_customer PRIMARY KEY (id),
    CONSTRAINT uq_customer_email UNIQUE (email),
    -- emails are normalised to lowercase in the domain, so UNIQUE is effectively case-insensitive
    CONSTRAINT ck_customer_email_lowercase CHECK (email = LOWER(email))
);

CREATE TABLE item
(
    id          UUID           NOT NULL,
    name        VARCHAR(100)   NOT NULL,
    description VARCHAR(255)   NOT NULL,
    price       NUMERIC(12, 2) NOT NULL,
    stock       INTEGER        NOT NULL,

    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT ck_item_price_positive CHECK (price > 0),
    CONSTRAINT ck_item_stock_not_negative CHECK (stock >= 0)
);

CREATE TABLE eurder
(
    id           UUID         NOT NULL,
    customer_id  UUID         NOT NULL,
    order_date   DATE         NOT NULL,
    -- shipping address snapshot, see decision in #23
    street       VARCHAR(100) NOT NULL,
    house_number VARCHAR(20)  NOT NULL,
    postal_code  VARCHAR(20)  NOT NULL,
    city         VARCHAR(100) NOT NULL,
    country      VARCHAR(100) NOT NULL,

    CONSTRAINT pk_eurder PRIMARY KEY (id),
    CONSTRAINT fk_eurder_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE TABLE item_group
(
    id             UUID           NOT NULL,
    eurder_id      UUID           NOT NULL,
    item_id        UUID           NOT NULL,
    -- item name and price are snapshots taken when the order is placed (story 3)
    item_name      VARCHAR(100)   NOT NULL,
    price_per_unit NUMERIC(12, 2) NOT NULL,
    amount         INTEGER        NOT NULL,
    shipping_date  DATE           NOT NULL,

    CONSTRAINT pk_item_group PRIMARY KEY (id),
    CONSTRAINT fk_item_group_eurder FOREIGN KEY (eurder_id) REFERENCES eurder (id),
    CONSTRAINT fk_item_group_item FOREIGN KEY (item_id) REFERENCES item (id),
    CONSTRAINT ck_item_group_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_item_group_price_per_unit_positive CHECK (price_per_unit > 0)
);

CREATE INDEX idx_eurder_customer_id ON eurder (customer_id);
CREATE INDEX idx_item_group_eurder_id ON item_group (eurder_id);
CREATE INDEX idx_item_group_item_id ON item_group (item_id);
CREATE INDEX idx_item_group_shipping_date ON item_group (shipping_date);