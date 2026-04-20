-- V2: Inventory schema — sequences, tables, constraints, indexes
-- =============================================================

-- ---------------------------------------------------------------
-- Sequences
-- One schema-wide sequence ensures all primary keys in inv are unique.
-- ---------------------------------------------------------------
CREATE SEQUENCE inv.sq_inv_id START 1 INCREMENT 1;

-- ---------------------------------------------------------------
-- Tables
-- ---------------------------------------------------------------

CREATE TABLE inv.categories (
    id          BIGINT          NOT NULL DEFAULT nextval('inv.sq_inv_id'),
    name        VARCHAR(100)    NOT NULL,
    parent_id   BIGINT,
    description TEXT,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE inv.brands (
    id            BIGINT       NOT NULL DEFAULT nextval('inv.sq_inv_id'),
    name          VARCHAR(100) NOT NULL,
    website       VARCHAR(255),
    country_code  CHAR(2),
    founded_year  SMALLINT
);

CREATE TABLE inv.products (
    id           BIGINT          NOT NULL DEFAULT nextval('inv.sq_inv_id'),
    sku          VARCHAR(50)     NOT NULL,
    name         VARCHAR(255)    NOT NULL,
    description  TEXT,
    category_id  BIGINT          NOT NULL,
    brand_id     BIGINT          NOT NULL,
    unit_price   NUMERIC(10,2)   NOT NULL,
    is_active    BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE inv.stock (
    id                  BIGINT  NOT NULL DEFAULT nextval('inv.sq_inv_id'),
    product_id          BIGINT  NOT NULL,
    quantity            INT     NOT NULL DEFAULT 0,
    reserved_quantity   INT     NOT NULL DEFAULT 0,
    warehouse_location  TEXT,
    last_updated        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE inv.price_history (
    id          BIGINT          NOT NULL DEFAULT nextval('inv.sq_inv_id'),
    product_id  BIGINT          NOT NULL,
    price       NUMERIC(10,2)   NOT NULL,
    valid_from  TIMESTAMPTZ     NOT NULL,
    valid_to    TIMESTAMPTZ
);

CREATE TABLE inv.product_tags (
    id          BIGINT       NOT NULL DEFAULT nextval('inv.sq_inv_id'),
    product_id  BIGINT       NOT NULL,
    tag         VARCHAR(50)  NOT NULL
);

-- ---------------------------------------------------------------
-- Primary Keys
-- ---------------------------------------------------------------
ALTER TABLE inv.categories    ADD CONSTRAINT pk_categories_id    PRIMARY KEY (id);
ALTER TABLE inv.brands        ADD CONSTRAINT pk_brands_id        PRIMARY KEY (id);
ALTER TABLE inv.products      ADD CONSTRAINT pk_products_id      PRIMARY KEY (id);
ALTER TABLE inv.stock         ADD CONSTRAINT pk_stock_id         PRIMARY KEY (id);
ALTER TABLE inv.price_history ADD CONSTRAINT pk_price_history_id PRIMARY KEY (id);
ALTER TABLE inv.product_tags  ADD CONSTRAINT pk_product_tags_id  PRIMARY KEY (id);

-- ---------------------------------------------------------------
-- Unique Constraints
-- ---------------------------------------------------------------
ALTER TABLE inv.products     ADD CONSTRAINT uc_products_sku                UNIQUE (sku);
ALTER TABLE inv.stock        ADD CONSTRAINT uc_stock_product_id            UNIQUE (product_id);
ALTER TABLE inv.product_tags ADD CONSTRAINT uc_product_tags_product_id_tag UNIQUE (product_id, tag);

-- ---------------------------------------------------------------
-- Foreign Keys
-- ---------------------------------------------------------------
ALTER TABLE inv.categories
    ADD CONSTRAINT fk_categories_parent_id
    FOREIGN KEY (parent_id) REFERENCES inv.categories (id)
    ON DELETE RESTRICT;

ALTER TABLE inv.products
    ADD CONSTRAINT fk_products_category_id
    FOREIGN KEY (category_id) REFERENCES inv.categories (id)
    ON DELETE RESTRICT;

ALTER TABLE inv.products
    ADD CONSTRAINT fk_products_brand_id
    FOREIGN KEY (brand_id) REFERENCES inv.brands (id)
    ON DELETE RESTRICT;

ALTER TABLE inv.stock
    ADD CONSTRAINT fk_stock_product_id
    FOREIGN KEY (product_id) REFERENCES inv.products (id)
    ON DELETE RESTRICT;

ALTER TABLE inv.price_history
    ADD CONSTRAINT fk_price_history_product_id
    FOREIGN KEY (product_id) REFERENCES inv.products (id)
    ON DELETE RESTRICT;

ALTER TABLE inv.product_tags
    ADD CONSTRAINT fk_product_tags_product_id
    FOREIGN KEY (product_id) REFERENCES inv.products (id)
    ON DELETE RESTRICT;

-- ---------------------------------------------------------------
-- Check Constraints
-- ---------------------------------------------------------------
ALTER TABLE inv.brands
    ADD CONSTRAINT chk_brands_founded_year
    CHECK (founded_year > 1800);

ALTER TABLE inv.products
    ADD CONSTRAINT chk_products_unit_price
    CHECK (unit_price > 0);

ALTER TABLE inv.stock
    ADD CONSTRAINT chk_stock_quantity
    CHECK (quantity >= 0);

ALTER TABLE inv.stock
    ADD CONSTRAINT chk_stock_reserved_quantity
    CHECK (reserved_quantity >= 0);

ALTER TABLE inv.price_history
    ADD CONSTRAINT chk_price_history_valid_range
    CHECK (valid_to IS NULL OR valid_from < valid_to);

-- ---------------------------------------------------------------
-- Indexes
-- ---------------------------------------------------------------
CREATE INDEX ix_products_category_id              ON inv.products      (category_id);
CREATE INDEX ix_products_brand_id                 ON inv.products      (brand_id);
CREATE INDEX ix_products_is_active_category_id    ON inv.products      (is_active, category_id);
CREATE INDEX ix_products_name                     ON inv.products      (name);
CREATE INDEX ix_stock_product_id                  ON inv.stock         (product_id);
CREATE INDEX ix_price_history_product_id_valid_from ON inv.price_history (product_id, valid_from);
CREATE INDEX ix_product_tags_product_id           ON inv.product_tags  (product_id);
