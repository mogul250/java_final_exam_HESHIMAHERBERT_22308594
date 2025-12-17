-- Finance Platform Manager (FPM) Database Schema

CREATE DATABASE IF NOT EXISTS bfpm_db;
USE bfpm_db;

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    user_role ENUM('Admin', 'DataClerk') NOT NULL
);

-- Chart of Accounts table
CREATE TABLE chart_of_accounts (
    account_id INT PRIMARY KEY AUTO_INCREMENT,
    account_name VARCHAR(100) UNIQUE NOT NULL,
    account_type ENUM('Revenue', 'Expense') NOT NULL
);

-- Transactions table
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    account_id INT NOT NULL,
    recorded_by_user_id INT NOT NULL,
    FOREIGN KEY (account_id) REFERENCES chart_of_accounts(account_id),
    FOREIGN KEY (recorded_by_user_id) REFERENCES users(user_id)
);

-- Insert sample data
INSERT INTO users (username, password_hash, user_role) VALUES
('admin', 'admin123', 'Admin'),
('clerk1', 'clerk123', 'DataClerk');

INSERT INTO chart_of_accounts (account_name, account_type) VALUES
('Sales Revenue', 'Revenue'),
('Service Revenue', 'Revenue'),
('Office Supplies', 'Expense'),
('Rent', 'Expense'),
('Utilities', 'Expense');
