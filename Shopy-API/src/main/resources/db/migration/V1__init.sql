CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    quantity INTEGER NOT NULL,
    location_x INTEGER NOT NULL,
    location_y INTEGER NOT NULL,
    CONSTRAINT unique_name_location UNIQUE (name, location_x, location_y)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    order_id BIGINT,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE order_route (
    order_id BIGINT NOT NULL,
    route_x INTEGER NOT NULL,
    route_y INTEGER NOT NULL,
    route_order INTEGER NOT NULL,
    PRIMARY KEY (order_id, route_order),
    CONSTRAINT fk_route_order FOREIGN KEY (order_id) REFERENCES orders(id)
); 