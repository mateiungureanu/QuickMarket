CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    user_type ENUM('admin', 'customer', 'seller') NOT NULL
);

CREATE TABLE customer (
    customer_id INT PRIMARY KEY,
    total_quantity INT DEFAULT 0,
    total_spent DOUBLE DEFAULT 0,
    FOREIGN KEY (customer_id) REFERENCES user(user_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE seller (
    seller_id INT PRIMARY KEY,
    total_quantity DOUBLE DEFAULT 0,
    total_revenue DOUBLE DEFAULT 0,
    FOREIGN KEY (seller_id) REFERENCES user(user_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE market (
    market_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100) NOT NULL
);

CREATE TABLE stall (
    stall_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    seller_id INT NOT NULL,
    market_id INT NOT NULL,
    FOREIGN KEY (seller_id) REFERENCES seller(seller_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (market_id) REFERENCES market(market_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    stall_id INT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (stall_id) REFERENCES stall(stall_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE shopping_list (
    shopping_list_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    UNIQUE KEY unique_customer_product (customer_id, product_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE purchase_history (
    purchase_history_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    product_id INT NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    stall_id INT NOT NULL,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
	FOREIGN KEY (stall_id) REFERENCES stall(stall_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    from_user_id INT NOT NULL,
    to_user_id INT NOT NULL,
    product_name VARCHAR(255),
    product_quantity INT,
    type VARCHAR(32) NOT NULL,
    status_customer VARCHAR(32) DEFAULT 'uncompleted',
    status_seller VARCHAR(32) DEFAULT 'unresponded',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES user(user_id),
    FOREIGN KEY (to_user_id) REFERENCES user(user_id)
);

INSERT INTO user (username, email, password, user_type) VALUES 
('admin', 'admin@quickmarket.com', 'admin', 'ADMIN');

INSERT INTO market (name, location) VALUES 
('Racari', 'Sector 3'),
('Bolovani', 'Ilfov');

INSERT INTO user (username, email, password, user_type) VALUES 
('nea gigel', 'neagigel@gmail.com', 'nea gigel', 'SELLER'),
('tanti aglaia', 'tantiaglaia@gmail.com', 'tanti aglaia', 'SELLER');

INSERT INTO user (username, email, password, user_type) VALUES 
('matei', 'matei@gmail.com', 'matei', 'CUSTOMER');

INSERT INTO seller (seller_id, total_quantity, total_revenue) VALUES 
(2, 0, 0),
(3, 0, 0);

INSERT INTO customer (customer_id, total_quantity, total_spent) VALUES 
(4, 0, 0);

INSERT INTO stall (name, market_id, seller_id) VALUES 
('Cocioaba lui nea Gigel', 2, 2),
('La tanti Aglaia', 2, 3);

INSERT INTO product (name, price, quantity, stall_id, deleted) VALUES 
('Visine', 7, 20, 1, false),
('Cirese', 5, 15, 1, false),
('Cartofi', 2.5, 25, 2, false),
('Castraveti', 2, 10, 2, false),
('Varza', 10, 12, 2, false);