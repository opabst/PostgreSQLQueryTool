-- V4: PL/pgSQL functions, triggers, and schema role grants
-- =============================================================

-- ---------------------------------------------------------------
-- inv.fn_get_available_stock
-- Returns quantity - reserved_quantity for a given product.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION inv.fn_get_available_stock(p_product_id BIGINT)
RETURNS INT
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_available INT;
BEGIN
    SELECT quantity - reserved_quantity
      INTO v_available
      FROM inv.stock
     WHERE product_id = p_product_id;

    RETURN COALESCE(v_available, 0);
END;
$$;

-- ---------------------------------------------------------------
-- inv.fn_update_product_timestamp
-- Trigger function: keeps inv.products.updated_at current.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION inv.fn_update_product_timestamp()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at := NOW();
    RETURN NEW;
END;
$$;

-- ---------------------------------------------------------------
-- cust.fn_check_and_deduct_stock
-- Trigger function: BEFORE INSERT on cust.order_items.
-- Raises an exception when available stock is insufficient,
-- otherwise decrements inv.stock.quantity.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION cust.fn_check_and_deduct_stock()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_available INT;
BEGIN
    v_available := inv.fn_get_available_stock(NEW.product_id);

    IF NEW.quantity > v_available THEN
        RAISE EXCEPTION
            'Insufficient stock for product %. Requested: %, Available: %',
            NEW.product_id, NEW.quantity, v_available;
    END IF;

    UPDATE inv.stock
       SET quantity     = quantity - NEW.quantity,
           last_updated = NOW()
     WHERE product_id = NEW.product_id;

    RETURN NEW;
END;
$$;

-- ---------------------------------------------------------------
-- cust.fn_recalculate_order_total
-- Trigger function: AFTER INSERT/UPDATE/DELETE on cust.order_items.
-- Recalculates the parent order's total_amount.
-- ---------------------------------------------------------------
CREATE OR REPLACE FUNCTION cust.fn_recalculate_order_total()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_order_id BIGINT;
BEGIN
    -- Determine the affected order id (DELETE uses OLD, INSERT/UPDATE uses NEW)
    IF TG_OP = 'DELETE' THEN
        v_order_id := OLD.order_id;
    ELSE
        v_order_id := NEW.order_id;
    END IF;

    UPDATE cust.orders
       SET total_amount = (
               SELECT COALESCE(
                          SUM(quantity * unit_price * (1 - discount_pct / 100.0)),
                          0
                      )
                 FROM cust.order_items
                WHERE order_id = v_order_id
           )
     WHERE id = v_order_id;

    RETURN NULL;
END;
$$;

-- ---------------------------------------------------------------
-- Triggers
-- ---------------------------------------------------------------

CREATE TRIGGER trg_products_updated_at
    BEFORE UPDATE ON inv.products
    FOR EACH ROW
    EXECUTE FUNCTION inv.fn_update_product_timestamp();

CREATE TRIGGER trg_order_items_stock
    BEFORE INSERT ON cust.order_items
    FOR EACH ROW
    EXECUTE FUNCTION cust.fn_check_and_deduct_stock();

CREATE TRIGGER trg_order_items_total
    AFTER INSERT OR UPDATE OR DELETE ON cust.order_items
    FOR EACH ROW
    EXECUTE FUNCTION cust.fn_recalculate_order_total();

-- ---------------------------------------------------------------
-- role_inv_connect — SELECT-only on inv
-- ---------------------------------------------------------------
GRANT USAGE ON SCHEMA inv TO role_inv_connect;
GRANT SELECT ON ALL TABLES    IN SCHEMA inv TO role_inv_connect;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA inv TO role_inv_connect;
ALTER DEFAULT PRIVILEGES IN SCHEMA inv GRANT SELECT    ON TABLES    TO role_inv_connect;
ALTER DEFAULT PRIVILEGES IN SCHEMA inv GRANT EXECUTE   ON FUNCTIONS TO role_inv_connect;

-- ---------------------------------------------------------------
-- role_inv_admin — full DML on inv
-- ---------------------------------------------------------------
GRANT USAGE ON SCHEMA inv TO role_inv_admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA inv TO role_inv_admin;
GRANT EXECUTE                         ON ALL FUNCTIONS IN SCHEMA inv TO role_inv_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA inv GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES    TO role_inv_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA inv GRANT EXECUTE                         ON FUNCTIONS TO role_inv_admin;

-- ---------------------------------------------------------------
-- role_cust_connect — SELECT-only on cust
-- ---------------------------------------------------------------
GRANT USAGE ON SCHEMA cust TO role_cust_connect;
GRANT SELECT ON ALL TABLES    IN SCHEMA cust TO role_cust_connect;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA cust TO role_cust_connect;
ALTER DEFAULT PRIVILEGES IN SCHEMA cust GRANT SELECT    ON TABLES    TO role_cust_connect;
ALTER DEFAULT PRIVILEGES IN SCHEMA cust GRANT EXECUTE   ON FUNCTIONS TO role_cust_connect;

-- ---------------------------------------------------------------
-- role_cust_admin — full DML on cust
-- ---------------------------------------------------------------
GRANT USAGE ON SCHEMA cust TO role_cust_admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA cust TO role_cust_admin;
GRANT EXECUTE                         ON ALL FUNCTIONS IN SCHEMA cust TO role_cust_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA cust GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES    TO role_cust_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA cust GRANT EXECUTE                         ON FUNCTIONS TO role_cust_admin;
