-- Reset product IDs to be user-specific
-- This will be handled by the DataInitializer when app.initialize.data=true

-- Drop the auto-increment sequence if it exists
DROP SEQUENCE IF EXISTS product_id_seq;

-- Update the product table to remove auto-increment
ALTER TABLE product ALTER COLUMN id DROP IDENTITY IF EXISTS; 