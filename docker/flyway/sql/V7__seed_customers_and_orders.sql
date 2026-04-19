-- V7: Seed customers, addresses, orders, and order items
-- ========================================================

DO $$
DECLARE
    -- ---------------------------------------------------------------
    -- Name pools
    -- ---------------------------------------------------------------
    first_names TEXT[] := ARRAY[
        'James','John','Robert','Michael','William','David','Richard','Joseph',
        'Thomas','Charles','Christopher','Daniel','Matthew','Anthony','Mark',
        'Donald','Steven','Paul','Andrew','Joshua','Kenneth','Kevin','Brian',
        'George','Timothy','Ronald','Edward','Jason','Jeffrey','Ryan',
        'Mary','Patricia','Jennifer','Linda','Barbara','Elizabeth','Susan',
        'Jessica','Sarah','Karen','Lisa','Nancy','Betty','Margaret','Sandra',
        'Ashley','Dorothy','Kimberly','Emily','Donna'
    ];

    last_names TEXT[] := ARRAY[
        'Smith','Johnson','Williams','Brown','Jones','Garcia','Miller','Davis',
        'Rodriguez','Martinez','Hernandez','Lopez','Gonzalez','Wilson','Anderson',
        'Thomas','Taylor','Moore','Jackson','Martin','Lee','Perez','Thompson',
        'White','Harris','Sanchez','Clark','Ramirez','Lewis','Robinson',
        'Walker','Young','Allen','King','Wright','Scott','Torres','Nguyen',
        'Hill','Flores','Green','Adams','Nelson','Baker','Hall','Rivera',
        'Campbell','Mitchell','Carter','Roberts','Turner','Phillips','Evans',
        'Edwards','Collins','Stewart','Morris','Morales','Murphy','Cook',
        'Rogers','Peterson','Reed','Bailey','Bell','Cooper','Richardson',
        'Cox','Howard','Ward','Torres','Long','Foster','Sanders','Price',
        'Bennett','Wood','Barnes','Ross','Henderson','Coleman','Jenkins',
        'Perry','Powell','Patterson','Hughes','Flores','Washington','Butler',
        'Simmons','Foster','Gonzales','Bryant','Alexander','Russell','Griffin',
        'Diaz','Hayes','Myers','Ford','Hamilton','Graham','Sullivan','Wallace'
    ];

    email_domains TEXT[] := ARRAY[
        'gmail.com','yahoo.com','outlook.com','hotmail.com','icloud.com',
        'protonmail.com','web.de','gmx.de','t-online.de','freenet.de'
    ];

    -- ---------------------------------------------------------------
    -- Order status pool (weighted toward delivered)
    -- ---------------------------------------------------------------
    order_statuses TEXT[] := ARRAY[
        'delivered','delivered','delivered','delivered','delivered',
        'shipped','shipped',
        'processing',
        'pending',
        'cancelled'
    ];

    -- ---------------------------------------------------------------
    -- Working variables
    -- ---------------------------------------------------------------
    v_customer_id   BIGINT;
    v_address_id    BIGINT;
    v_order_id      BIGINT;
    v_item_id       BIGINT;
    v_product_id    BIGINT;
    v_first         TEXT;
    v_last          TEXT;
    v_email         TEXT;
    v_dob           DATE;
    v_tier          TEXT;
    v_tier_roll     INT;
    v_order_count   INT;
    v_item_count    INT;
    v_status        TEXT;
    v_unit_price    NUMERIC(10,2);
    v_i             INT;
    v_j             INT;
    v_k             INT;
    v_max_product   BIGINT;
BEGIN
    -- Determine the highest product id that has been seeded
    SELECT MAX(id) INTO v_max_product FROM inv.products;

    -- ---------------------------------------------------------------
    -- 500 customers
    -- ---------------------------------------------------------------
    FOR v_i IN 1..500 LOOP
        v_customer_id := nextval('cust.sq_customers_id');
        v_first       := first_names[1 + ((v_i - 1) % array_length(first_names, 1))];
        v_last        := last_names [1 + ((v_i - 1) % array_length(last_names,  1))];
        v_email       := lower(v_first) || '.' || lower(v_last)
                          || v_customer_id::TEXT || '@'
                          || email_domains[1 + ((v_i - 1) % array_length(email_domains, 1))];

        -- DOB: random date between 1950-01-01 and 2006-01-01
        v_dob := '1950-01-01'::DATE
                  + (((v_i * 103 + 17) % 20453))::INT;

        -- Tier distribution: 70 % bronze, 20 % silver, 8 % gold, 2 % platinum
        v_tier_roll := (v_i % 100);
        IF    v_tier_roll < 2  THEN v_tier := 'platinum';
        ELSIF v_tier_roll < 10 THEN v_tier := 'gold';
        ELSIF v_tier_roll < 30 THEN v_tier := 'silver';
        ELSE                        v_tier := 'bronze';
        END IF;

        INSERT INTO cust.customers
            (id, first_name, last_name, email, date_of_birth, customer_tier)
        VALUES
            (v_customer_id, v_first, v_last, v_email, v_dob, v_tier);

        -- One billing address per customer
        v_address_id := nextval('cust.sq_addresses_id');

        INSERT INTO cust.addresses
            (id, customer_id, type, street, city, state, postal_code, country_code, is_default)
        VALUES (
            v_address_id,
            v_customer_id,
            'billing',
            v_i::TEXT || ' Main Street',
            (ARRAY['Berlin','Munich','Hamburg','Frankfurt','Cologne','Stuttgart',
                   'Düsseldorf','Leipzig','Dortmund','Essen',
                   'New York','Los Angeles','Chicago','Houston','Phoenix',
                   'London','Paris','Madrid','Rome','Vienna'])[1 + ((v_i - 1) % 20)],
            NULL,
            lpad((10000 + (v_i * 7 % 89999))::TEXT, 5, '0'),
            (ARRAY['DE','DE','DE','DE','DE','US','US','US','GB','FR',
                   'AT','ES','IT','NL','PL','SE','CH','BE','DK','NO'])[1 + ((v_i - 1) % 20)],
            TRUE
        );
    END LOOP;

    -- ---------------------------------------------------------------
    -- ~200 orders spread across customers
    -- ---------------------------------------------------------------
    v_order_count := 0;
    v_k := 0;
    FOR v_i IN 1..200 LOOP
        -- Distribute orders across customers 1..500
        v_customer_id := 1 + ((v_i * 7 + 13) % 500);

        -- Find the billing address for this customer
        SELECT id INTO v_address_id
          FROM cust.addresses
         WHERE customer_id = v_customer_id
         LIMIT 1;

        v_status  := order_statuses[1 + ((v_i - 1) % array_length(order_statuses, 1))];

        v_order_id := nextval('cust.sq_orders_id');

        INSERT INTO cust.orders
            (id, customer_id, status, order_date, shipping_address_id, notes)
        VALUES (
            v_order_id,
            v_customer_id,
            v_status,
            NOW() - ((v_i % 365) || ' days')::INTERVAL,
            v_address_id,
            NULL
        );

        -- ---------------------------------------------------------------
        -- 4 order items per order  →  ~800 items total
        -- ---------------------------------------------------------------
        FOR v_j IN 1..4 LOOP
            -- Deterministic but varied product selection
            v_product_id := 1 + (((v_i * 31 + v_j * 97 + v_k) % v_max_product)::BIGINT);
            v_k := v_k + 1;

            -- Fetch the product's list price
            SELECT unit_price INTO v_unit_price
              FROM inv.products
             WHERE id = v_product_id;

            -- Skip if product somehow doesn't exist (defensive guard)
            IF v_unit_price IS NULL THEN
                CONTINUE;
            END IF;

            v_item_id := nextval('cust.sq_order_items_id');

            -- Quantity between 1 and 3 (well within stock of 500)
            INSERT INTO cust.order_items
                (id, order_id, product_id, quantity, unit_price, discount_pct)
            VALUES (
                v_item_id,
                v_order_id,
                v_product_id,
                1 + (v_j % 3),
                v_unit_price,
                (ARRAY[0,0,0,5,10,15])[1 + ((v_i + v_j) % 6)]
            );
        END LOOP;

        v_order_count := v_order_count + 1;
    END LOOP;

END;
$$;
