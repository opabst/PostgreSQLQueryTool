-- This script runs once when the PostgreSQL container is first initialised.
-- It creates per-schema roles and login users for the inv and cust schemas.
-- Schema-level GRANTs are separated into roles (no-login) and then assigned
-- to login users, keeping privilege management independent of user identities.
-- Actual schema-level GRANTs are deferred to Flyway migration V4.

-- ---------------------------------------------------------------
-- Roles (no login) — hold the actual privileges
-- ---------------------------------------------------------------
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'role_inv_admin')   THEN CREATE ROLE role_inv_admin;   END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'role_inv_connect') THEN CREATE ROLE role_inv_connect; END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'role_cust_admin')  THEN CREATE ROLE role_cust_admin;  END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'role_cust_connect')THEN CREATE ROLE role_cust_connect;END IF;
END $$;

-- ---------------------------------------------------------------
-- Login users — receive roles, hold no direct privileges
-- ---------------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'inv_admin') THEN
        CREATE USER inv_admin WITH PASSWORD 'inv_admin_pw';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'inv_user') THEN
        CREATE USER inv_user WITH PASSWORD 'inv_user_pw';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'cust_admin') THEN
        CREATE USER cust_admin WITH PASSWORD 'cust_admin_pw';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'cust_user') THEN
        CREATE USER cust_user WITH PASSWORD 'cust_user_pw';
    END IF;
END;
$$;

-- ---------------------------------------------------------------
-- Assign roles to login users
-- ---------------------------------------------------------------
GRANT role_inv_admin   TO inv_admin;
GRANT role_inv_connect TO inv_user;
GRANT role_cust_admin  TO cust_admin;
GRANT role_cust_connect TO cust_user;

-- ---------------------------------------------------------------
-- Database-level CONNECT (schema-level GRANTs in Flyway V4)
-- ---------------------------------------------------------------
GRANT CONNECT ON DATABASE pqt_shop TO inv_admin;
GRANT CONNECT ON DATABASE pqt_shop TO inv_user;
GRANT CONNECT ON DATABASE pqt_shop TO cust_admin;
GRANT CONNECT ON DATABASE pqt_shop TO cust_user;
