package com.bfpm.dbmanager;

import com.bfpm.model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDBManager {

    public void saveTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (date, description, amount, account_id, recorded_by_user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(transaction.getDate()));
            stmt.setString(2, transaction.getDescription());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setInt(4, transaction.getAccountId());
            stmt.setInt(5, transaction.getRecordedByUserId());
            stmt.executeUpdate();
        }
    }

    public Transaction getTransactionById(int id) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Transaction(rs.getInt("transaction_id"), rs.getDate("date").toLocalDate(),
                                      rs.getString("description"), rs.getBigDecimal("amount"),
                                      rs.getInt("account_id"), rs.getInt("recorded_by_user_id"));
            }
        }
        return null;
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(new Transaction(rs.getInt("transaction_id"), rs.getDate("date").toLocalDate(),
                                                rs.getString("description"), rs.getBigDecimal("amount"),
                                                rs.getInt("account_id"), rs.getInt("recorded_by_user_id")));
            }
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByUser(int userId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE recorded_by_user_id = ? ORDER BY date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(rs.getInt("transaction_id"), rs.getDate("date").toLocalDate(),
                                                rs.getString("description"), rs.getBigDecimal("amount"),
                                                rs.getInt("account_id"), rs.getInt("recorded_by_user_id")));
            }
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByDateRange(java.time.LocalDate start, java.time.LocalDate end) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(rs.getInt("transaction_id"), rs.getDate("date").toLocalDate(),
                                                rs.getString("description"), rs.getBigDecimal("amount"),
                                                rs.getInt("account_id"), rs.getInt("recorded_by_user_id")));
            }
        }
        return transactions;
    }

    public void updateTransaction(Transaction transaction) throws SQLException {
        String sql = "UPDATE transactions SET date = ?, description = ?, amount = ?, account_id = ? WHERE transaction_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(transaction.getDate()));
            stmt.setString(2, transaction.getDescription());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setInt(4, transaction.getAccountId());
            stmt.setInt(5, transaction.getTransactionId());
            stmt.executeUpdate();
        }
    }

    public void deleteTransaction(int id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
