INSERT INTO pivots (pivot_pk, pivot_key, pivot_name, error_status, rotation) VALUES(1, 'A', 'Shorty', 'OK', '0.253');
INSERT INTO pivots (pivot_pk, pivot_key, pivot_name, error_status, rotation) VALUES(2, 'B', 'NorthOfHouse', 'OK', '0.785');
INSERT INTO pivots (pivot_pk, pivot_key, pivot_name, error_status, rotation) VALUES(3, 'C', 'SouthOfHouse', 'OK', '254.093');
INSERT INTO pivots (pivot_pk, pivot_key, pivot_name, error_status, rotation) VALUES(4, 'D', 'NewTnL', 'OK', '123.742');
INSERT INTO pivots (pivot_pk, pivot_key, pivot_name, error_status, rotation) VALUES(5, 'E', 'BigCircle', 'OK', '327.293');

INSERT INTO customers (customer_pk, customer_key, customer_name) VALUES(1, 'A', 'Bob');
INSERT INTO customers (customer_pk, customer_key, customer_name) VALUES(2, 'B', 'Fred');
INSERT INTO customers (customer_pk, customer_key, customer_name) VALUES(3, 'C', 'Steve');
INSERT INTO customers (customer_pk, customer_key, customer_name) VALUES(4, 'D', 'Jones');

INSERT INTO contacts (customer_fk, contact_key, description, email) VALUES(1, 'A', 'Work Email', 'bob@banking.net');
INSERT INTO contacts (customer_fk, contact_key, description, email) VALUES(1, 'B', 'Personal Email', 'bob@gmail.org');
INSERT INTO contacts (customer_fk, contact_key, description, email) VALUES(2, 'C', 'Work Email', 'fred@banking.net');
INSERT INTO contacts (customer_fk, contact_key, description, email) VALUES(3, 'D', 'Work Email', 'steve@banking.net');
INSERT INTO contacts (customer_fk, contact_key, description, email) VALUES(4, 'E', 'Work Email', 'jones@banking.net');
INSERT INTO contacts (customer_fk, contact_key, description, email) VALUES(4, 'F', 'Personal Email', 'jones@hotmail.gov');

INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(1, 2, 'A');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(1, 3, 'B');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(2, 1, 'C');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(3, 1, 'D');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(3, 4, 'E');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(4, 1, 'F');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(4, 2, 'G');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(4, 3, 'H');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(4, 4, 'I');
INSERT INTO customer_pivots (customer_fk, pivot_fk, public_key) VALUES(4, 5, 'J');