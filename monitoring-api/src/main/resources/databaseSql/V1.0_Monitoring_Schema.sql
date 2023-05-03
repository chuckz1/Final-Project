DROP TABLE IF EXISTS customer_pivots;
DROP TABLE IF EXISTS contacts;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS pivots;


CREATE TABLE pivots (
  pivot_pk INT unsigned NOT NULL AUTO_INCREMENT,
  pivot_key VARCHAR(10) NOT NULL,
  pivot_name VARCHAR(50),
  error_status ENUM('OK', 'ALL', 'WATER', 'HYDRAULIC') NOT NULL DEFAULT 'OK',
  rotation DECIMAL(6, 3) NOT null,
  PRIMARY KEY (pivot_pk)
);

CREATE TABLE customers (
  customer_pk INT unsigned NOT NULL AUTO_INCREMENT,
  customer_key VARCHAR(10) NOT NULL,
  customer_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (customer_pk)
);

CREATE TABLE contacts (
  contact_pk INT unsigned NOT NULL AUTO_INCREMENT,
  customer_fk INT unsigned NOT NULL,
  contact_key VARCHAR(10) not null,
  description VARCHAR(50),
  email VARCHAR(50) NOT NULL,
  PRIMARY KEY (contact_pk),
  FOREIGN KEY (customer_fk) REFERENCES customers (customer_pk) ON DELETE CASCADE
);

CREATE TABLE customer_pivots (
  customer_fk INT unsigned NOT NULL,
  pivot_fk INT unsigned NOT NULL,
  public_key VARCHAR(10) NOT NULL,
  PRIMARY KEY (public_key),
  FOREIGN KEY (customer_fk) REFERENCES customers (customer_pk) ON DELETE CASCADE,
  FOREIGN KEY (pivot_fk) REFERENCES pivots (pivot_pk) ON DELETE CASCADE
);