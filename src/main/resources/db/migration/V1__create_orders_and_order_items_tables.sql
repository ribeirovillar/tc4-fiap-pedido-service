-- V1__create_orders_and_order_items_tables.sql

CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    customer_name VARCHAR(255),
    customer_cpf VARCHAR(11),
    card_number VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_id UUID,
    payment_status VARCHAR(50),
    payment_amount DECIMAL(19,2)
);

CREATE TABLE order_items (
    sku VARCHAR(255),
    order_id UUID NOT NULL,
    name VARCHAR(255),
    quantity INTEGER NOT NULL,
    price DECIMAL(19,2),
    PRIMARY KEY (sku, order_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);