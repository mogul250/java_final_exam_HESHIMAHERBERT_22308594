# Basic Finance Platform Manager (BFPM)

## Project Description

Basic Finance Platform Manager (BFPM) is a Java Swing-based desktop application designed for managing basic finance operations in a small business or organization. It provides a user-friendly interface for recording transactions, generating financial reports, and managing users and accounts with role-based access control. The application uses MySQL for data storage and supports two user roles: Admin and DataClerk.

## Features

- **User Authentication**: Secure login system with username and password.
- **Transaction Management**: Create, read, update, and delete financial transactions linked to chart of accounts.
- **Financial Reports**: Generate Profit & Loss (P&L) reports and Expense Trend reports.
- **Role-Based Access**:
  - **DataClerk**: Can manage transactions and view reports.
  - **Admin**: Full access including user management and chart of accounts management.
- **Chart of Accounts Management**: Admin users can add and manage revenue and expense accounts.
- **User Management**: Admin users can create and manage user accounts with role assignments.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7 or higher
- MySQL Connector/J JAR (included in `lib/mysql-connector-java-8.0.30.jar`)

## Setup Instructions

1. **Database Setup**:
   - Install and start MySQL Server.
   - Create a database user with privileges to create databases and tables (or use root).
   - Run the `schema.sql` script to create the database and insert sample data:
     ```
     mysql -u your_username -p < schema.sql
     ```
     This will create the `bfpm_db` database with tables for users, chart_of_accounts, and transactions, along with sample users and accounts.

2. **Compile the Project**:
   - Ensure the MySQL Connector/J JAR is in your classpath.
   - Compile all Java files:
     ```
     javac -cp "lib/mysql-connector-java-8.0.30.jar" com/bfpm/**/*.java
     ```

3. **Run the Application**:
   - Execute the main class:
     ```
     java -cp ".:lib/mysql-connector-java-8.0.30.jar" com.bfpm.ui.MainApplication
     ```
     Note: On Windows, use `;` instead of `:` in the classpath.

## Usage

1. **Login**:
   - Launch the application.
   - Enter your username and password.
   - Sample credentials:
     - Admin: username `admin`, password `admin123`
     - DataClerk: username `clerk1`, password `clerk123`

2. **Navigation**:
   - After login, the main window displays tabs based on your role.
   - **Transactions Tab**: Record and manage financial transactions (date, description, amount, account).
   - **Reports Tab**: View P&L and Expense Trend reports.
   - **Users Tab** (Admin only): Manage user accounts and roles.
   - **Accounts Tab** (Admin only): Manage the chart of accounts.
   - Use the "Logout" button to return to the login screen.

3. **Performing Operations**:
   - In the Transactions tab, fill in the form to add new transactions.
   - In Reports, select report type and view generated data.
   - Admins can add new users or accounts via their respective tabs.

## Project Structure

- `com/bfpm/model/`: Data model classes (User.java, Account.java, Transaction.java)
- `com/bfpm/dbmanager/`: Database connectivity and CRUD operations (DBConnection.java, UserDBManager.java, etc.)
- `com/bfpm/service/`: Business logic for reports (ReportService.java)
- `com/bfpm/ui/`: Swing UI forms (MainApplication.java, TransactionForm.java, etc.)
- `lib/`: External libraries (MySQL Connector/J)
- `schema.sql`: Database schema and sample data
- `TODO.md`: Development tasks and progress

## Contributing/Notes

This project is a work in progress. Refer to `TODO.md` for current development status and planned enhancements, including input validation, error handling, and logging. Contributions are welcome; please ensure code follows the existing package structure and includes appropriate documentation.

