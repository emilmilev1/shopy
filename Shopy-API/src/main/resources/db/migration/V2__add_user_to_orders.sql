-- Add user_id column to orders table
ALTER TABLE orders ADD COLUMN user_id BIGINT;

-- Add foreign key constraint
ALTER TABLE orders ADD CONSTRAINT fk_orders_user 
    FOREIGN KEY (user_id) REFERENCES users(id);

-- Create index for better performance
CREATE INDEX idx_orders_user_id ON orders(user_id); 