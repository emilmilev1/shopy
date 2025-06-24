-- Add user_id column to product table
ALTER TABLE product ADD COLUMN user_id BIGINT;

-- Add foreign key constraint
ALTER TABLE product ADD CONSTRAINT fk_product_user 
    FOREIGN KEY (user_id) REFERENCES users(id);

-- Create index for better performance
CREATE INDEX idx_product_user_id ON product(user_id);

-- Drop the old unique constraint and create a new one that includes user_id
ALTER TABLE product DROP CONSTRAINT IF EXISTS product_name_location_x_location_y_key;
ALTER TABLE product ADD CONSTRAINT product_name_location_x_location_y_user_id_key 
    UNIQUE (name, location_x, location_y, user_id); 