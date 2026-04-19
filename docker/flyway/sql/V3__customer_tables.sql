-- V3: Customer schema — sequences, tables, constraints, indexes
-- =============================================================

-- ---------------------------------------------------------------
-- Sequences
-- One schema-wide sequence ensures all primary keys in cust are unique.
-- ---------------------------------------------------------------
CREATE SEQUENCE cust.sq_cust_id START 1 INCREMENT 1;

-- ---------------------------------------------------------------
-- Tables
-- ---------------------------------------------------------------

CREATE TABLE cust.customers (
    id                  BIGINT       NOT NULL DEFAULT nextval('cust.sq_cust_id'),
    first_name          VARCHAR(100) NOT NULL,
    last_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(255) NOT NULL,
    phone               VARCHAR(30),
    date_of_birth       DATE,
    registration_date   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    customer_tier       VARCHAR(10)  NOT NULL DEFAULT 'bronze'
);

CREATE TABLE cust.addresses (
    id            BIGINT       NOT NULL DEFAULT nextval('cust.sq_cust_id'),
    customer_id   BIGINT       NOT NULL,
    type          VARCHAR(10)  NOT NULL,
    street        VARCHAR(255) NOT NULL,
    city          VARCHAR(100) NOT NULL,
    state         VARCHAR(100),
    postal_code   VARCHAR(20)  NOT NULL,
    country_code  CHAR(2)      NOT NULL,
    is_default    BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE cust.orders (
    id                   BIGINT        NOT NULL DEFAULT nextval('cust.sq_cust_id'),
    order_date           TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    customer_id          BIGINT        NOT NULL,
    status               VARCHAR(20)   NOT NULL DEFAULT 'pending',
    shipping_address_id  BIGINT,
    total_amount         NUMERIC(12,2) NOT NULL DEFAULT 0,
    notes                TEXT
);

CREATE TABLE cust.order_items (
    id           BIGINT        NOT NULL DEFAULT nextval('cust.sq_cust_id'),
    order_id     BIGINT        NOT NULL,
    product_id   BIGINT        NOT NULL,
    quantity     INT           NOT NULL,
    unit_price   NUMERIC(10,2) NOT NULL,
    discount_pct NUMERIC(5,2)  NOT NULL DEFAULT 0
);

CREATE TABLE cust.payment_methods (
    id           BIGINT      NOT NULL DEFAULT nextval('cust.sq_cust_id'),
    customer_id  BIGINT      NOT NULL,
    type         VARCHAR(20) NOT NULL,
    last_four    CHAR(4),
    is_default   BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE cust.reviews (
    id           BIGINT        NOT NULL DEFAULT nextval('cust.sq_cust_id'),
    customer_id  BIGINT        NOT NULL,
    product_id   BIGINT        NOT NULL,
    rating       SMALLINT      NOT NULL,
    title        VARCHAR(200),
    body         TEXT,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    is_verified  BOOLEAN       NOT NULL DEFAULT FALSE
);

-- ---------------------------------------------------------------
-- Primary Keys
-- ---------------------------------------------------------------
ALTER TABLE cust.customers       ADD CONSTRAINT pk_customers_id       PRIMARY KEY (id);
ALTER TABLE cust.addresses       ADD CONSTRAINT pk_addresses_id       PRIMARY KEY (id);
ALTER TABLE cust.orders          ADD CONSTRAINT pk_orders_id          PRIMARY KEY (id);
ALTER TABLE cust.order_items     ADD CONSTRAINT pk_order_items_id     PRIMARY KEY (id);
ALTER TABLE cust.payment_methods ADD CONSTRAINT pk_payment_methods_id PRIMARY KEY (id);
ALTER TABLE cust.reviews         ADD CONSTRAINT pk_reviews_id         PRIMARY KEY (id);

-- ---------------------------------------------------------------
-- Unique Constraints
-- ---------------------------------------------------------------
ALTER TABLE cust.customers ADD CONSTRAINT uc_customers_email               UNIQUE (email);
ALTER TABLE cust.reviews   ADD CONSTRAINT uc_reviews_customer_id_product_id UNIQUE (customer_id, product_id);

-- ---------------------------------------------------------------
-- Foreign Keys
-- ---------------------------------------------------------------
ALTER TABLE cust.addresses
    ADD CONSTRAINT fk_addresses_customer_id
    FOREIGN KEY (customer_id) REFERENCES cust.customers (id);

ALTER TABLE cust.orders
    ADD CONSTRAINT fk_orders_customer_id
    FOREIGN KEY (customer_id) REFERENCES cust.customers (id);

ALTER TABLE cust.orders
    ADD CONSTRAINT fk_orders_shipping_address_id
    FOREIGN KEY (shipping_address_id) REFERENCES cust.addresses (id);

ALTER TABLE cust.order_items
    ADD CONSTRAINT fk_order_items_order_id
    FOREIGN KEY (order_id) REFERENCES cust.orders (id);

ALTER TABLE cust.order_items
    ADD CONSTRAINT fk_order_items_product_id
    FOREIGN KEY (product_id) REFERENCES inv.products (id);

ALTER TABLE cust.payment_methods
    ADD CONSTRAINT fk_payment_methods_customer_id
    FOREIGN KEY (customer_id) REFERENCES cust.customers (id);

ALTER TABLE cust.reviews
    ADD CONSTRAINT fk_reviews_customer_id
    FOREIGN KEY (customer_id) REFERENCES cust.customers (id);

ALTER TABLE cust.reviews
    ADD CONSTRAINT fk_reviews_product_id
    FOREIGN KEY (product_id) REFERENCES inv.products (id);

-- ---------------------------------------------------------------
-- Check Constraints
-- ---------------------------------------------------------------
ALTER TABLE cust.customers
    ADD CONSTRAINT chk_customers_tier
    CHECK (customer_tier IN ('bronze', 'silver', 'gold', 'platinum'));

ALTER TABLE cust.customers
    ADD CONSTRAINT chk_customers_date_of_birth
    CHECK (date_of_birth < CURRENT_DATE);

ALTER TABLE cust.addresses
    ADD CONSTRAINT chk_addresses_type
    CHECK (type IN ('billing', 'shipping'));

ALTER TABLE cust.orders
    ADD CONSTRAINT chk_orders_status
    CHECK (status IN ('pending', 'processing', 'shipped', 'delivered', 'cancelled'));

ALTER TABLE cust.orders
    ADD CONSTRAINT chk_orders_total_amount
    CHECK (total_amount >= 0);

ALTER TABLE cust.order_items
    ADD CONSTRAINT chk_order_items_quantity
    CHECK (quantity > 0);

ALTER TABLE cust.order_items
    ADD CONSTRAINT chk_order_items_unit_price
    CHECK (unit_price >= 0);

ALTER TABLE cust.order_items
    ADD CONSTRAINT chk_order_items_discount_pct
    CHECK (discount_pct BETWEEN 0 AND 100);

ALTER TABLE cust.payment_methods
    ADD CONSTRAINT chk_payment_methods_type
    CHECK (type IN ('credit_card', 'debit_card', 'paypal', 'bank_transfer'));

ALTER TABLE cust.reviews
    ADD CONSTRAINT chk_reviews_rating
    CHECK (rating BETWEEN 1 AND 5);

-- ---------------------------------------------------------------
-- Indexes
-- ---------------------------------------------------------------
CREATE INDEX ix_customers_last_name_first_name ON cust.customers    (last_name, first_name);
CREATE INDEX ix_orders_customer_id             ON cust.orders       (customer_id);
CREATE INDEX ix_orders_status_order_date       ON cust.orders       (status, order_date);
CREATE INDEX ix_order_items_order_id           ON cust.order_items  (order_id);
CREATE INDEX ix_order_items_product_id         ON cust.order_items  (product_id);
CREATE INDEX ix_reviews_product_id             ON cust.reviews      (product_id);
CREATE INDEX ix_addresses_customer_id          ON cust.addresses    (customer_id);
