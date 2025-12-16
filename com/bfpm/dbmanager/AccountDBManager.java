package com.bfpm.dbmanager;

import com.bfpm.model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDBManager {

    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM chart_of_accounts ORDER BY account_name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(new Account(rs.getInt("account_id"), rs.getString("account_name"),
                                       rs.getString("account_type")));
            }
        }
        return accounts;
    }

    public Account getAccountById(int id) throws SQLException {
        String sql = "SELECT * FROM chart_of_accounts WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("account_name"),
                                  rs.getString("account_type"));
            }
        }
        return null;
    }

    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO chart_of_accounts (account_name, account_type) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, account.getAccountName());
            stmt.setString(2, account.getAccountType());
            stmt.executeUpdate();
        }
    }

    public void updateAccount(Account account) throws SQLException {
        String sql = "UPDATE chart_of_accounts SET account_name = ?, account_type = ? WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, account.getAccountName());
            stmt.setString(2, account.getAccountType());
            stmt.setInt(3, account.getAccountId());
            stmt.executeUpdate();
        }
    }

    public void deleteAccount(int id) throws SQLException {
        String sql = "DELETE FROM chart_of_accounts WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
