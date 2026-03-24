-- ============================================================
--  Store Management System - Database Setup Script
--  Run this script in MySQL before starting the application.
-- ============================================================

-- Create and select the database
CREATE DATABASE IF NOT EXISTS store_management;
USE store_management;

-- ------------------------------------------------------------
-- Table: users
-- Stores login credentials and role for each user.
-- Roles: 'Admin' or 'StoreKeeper'
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(20)  NOT NULL
);

-- ------------------------------------------------------------
-- Table: products
-- Stores product catalogue with stock and supplier info.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id       INT            AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100)   NOT NULL,
    price    DECIMAL(10, 2) NOT NULL,
    quantity INT            NOT NULL DEFAULT 0,
    supplier VARCHAR(100)   NOT NULL
);

-- ------------------------------------------------------------
-- Table: sales
-- Records every sale transaction.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sales (
    id         INT            AUTO_INCREMENT PRIMARY KEY,
    product_id INT            NOT NULL,
    quantity   INT            NOT NULL,
    total      DECIMAL(10, 2) NOT NULL,
    date       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ------------------------------------------------------------
-- Seed Data: Default users
-- Default Admin  -> username: admin    | password: admin123
-- Default Keeper -> username: keeper   | password: keeper123
-- ------------------------------------------------------------
INSERT INTO users (username, password, role) VALUES
    ('admin',  'admin123',  'Admin'),
    ('keeper', 'keeper123', 'StoreKeeper');

-- ------------------------------------------------------------
-- Seed Data: Sample products
-- ------------------------------------------------------------
INSERT INTO products (name, price, quantity, supplier) VALUES
    ('Rice (1kg)',   55.00, 100, 'FreshFarm Traders'),
    ('Sugar (1kg)',  42.00,  80, 'SweetHarvest Co.'),
    ('Cooking Oil',  90.00,  50, 'NaturePure Oils'),
    ('Wheat Flour',  35.00,  60, 'GrainMills Ltd.');
