package com.bfpm.dbmanager;

import com.bfpm.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDBManager {

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Keep password raw as per requirement
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("user_id"), rs.getString("username"),
                               rs.getString("password_hash"), rs.getString("user_role"));
            }
        }
        return null;
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, user_role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getUserRole());
            stmt.executeUpdate();
        }
    }

    public void updateUserRole(int userId, String newRole) throws SQLException {
        String sql = "UPDATE users SET user_role = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(rs.getInt("user_id"), rs.getString("username"),
                                  rs.getString("password_hash"), rs.getString("user_role")));
            }
        }
        return users;
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}
