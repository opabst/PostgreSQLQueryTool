-- V1: Create application schemas
-- =============================================================

CREATE SCHEMA inv;
COMMENT ON SCHEMA inv IS 'Inventory: products, stock, pricing, categories, brands';

CREATE SCHEMA cust;
COMMENT ON SCHEMA cust IS 'Customers: accounts, addresses, orders, reviews, payments';
