CREATE TABLE user (
  id BIGINT AUTO_INCREMENT NOT NULL,
   user_name VARCHAR(255) NOT NULL,
   email VARCHAR(255) NOT NULL,
   phone VARCHAR(255) NULL,
   CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE product (
  id BIGINT AUTO_INCREMENT NOT NULL,
   product_name VARCHAR(255) NOT NULL,
   price DECIMAL NOT NULL,
   CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE customer_order (
  id BIGINT AUTO_INCREMENT NOT NULL,
   user_id BIGINT NOT NULL,
   order_amount DECIMAL NOT NULL,
   order_status VARCHAR(255) NULL,
   created_on datetime NULL,
   updated_on datetime NULL,
   CONSTRAINT pk_customerorder PRIMARY KEY (id)
);

ALTER TABLE customer_order ADD CONSTRAINT FK_CUSTOMERORDER_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

CREATE TABLE order_product (
  id BIGINT AUTO_INCREMENT NOT NULL,
   order_id BIGINT NOT NULL,
   product_id BIGINT NOT NULL,
   quantity INT NOT NULL,
   CONSTRAINT pk_orderproduct PRIMARY KEY (id)
);

ALTER TABLE order_product ADD CONSTRAINT FK_ORDERPRODUCT_ON_ORDER FOREIGN KEY (order_id) REFERENCES customer_order (id);

ALTER TABLE order_product ADD CONSTRAINT FK_ORDERPRODUCT_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

CREATE TABLE payment (
  id BIGINT AUTO_INCREMENT NOT NULL,
   order_id BIGINT NULL,
   user_id BIGINT NULL,
   received_amount DECIMAL NOT NULL,
   payment_status VARCHAR(255) NULL,
   error_message VARCHAR(255) NULL,
   created_on datetime NULL,
   updated_on datetime NULL,
   CONSTRAINT pk_payment PRIMARY KEY (id)
);

ALTER TABLE payment ADD CONSTRAINT FK_PAYMENT_ON_ORDER FOREIGN KEY (order_id) REFERENCES customer_order (id);

ALTER TABLE payment ADD CONSTRAINT FK_PAYMENT_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);

CREATE TABLE transaction (
  id BIGINT AUTO_INCREMENT NOT NULL,
   customer_order_id BIGINT NULL,
   user_id BIGINT NULL,
   amount DECIMAL NOT NULL,
   created_on datetime NULL,
   updated_on datetime NULL,
   CONSTRAINT pk_transaction PRIMARY KEY (id)
);

ALTER TABLE transaction ADD CONSTRAINT FK_TRANSACTION_ON_CUSTOMER_ORDER FOREIGN KEY (customer_order_id) REFERENCES customer_order (id);

ALTER TABLE transaction ADD CONSTRAINT FK_TRANSACTION_ON_USER FOREIGN KEY (user_id) REFERENCES user (id);