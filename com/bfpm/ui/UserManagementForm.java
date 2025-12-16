package com.bfpm.ui;

import com.bfpm.model.User;
import com.bfpm.dbmanager.UserDBManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class UserManagementForm extends JFrame {
    private UserDBManager userDBManager;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public UserManagementForm() {
        userDBManager = new UserDBManager();
        setTitle("Manage Users");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table for users
        String[] columns = {"ID", "Username", "Role"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit Role");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditRoleDialog());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsers());
    }

    private void loadUsers() {
        try {
            tableModel.setRowCount(0);
            List<User> users = userDBManager.getAllUsers();
            for (User u : users) {
                tableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getUserRole()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add User", true);
        dialog.setSize(300, 250);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "DataClerk"});
        JButton saveButton = new JButton("Save");

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dialog.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        dialog.add(roleCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(saveButton, gbc);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
                    String role = (String) roleCombo.getSelectedItem();
                    User user = new User(0, username, password, role);
                    userDBManager.createUser(user);
                    loadUsers();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error saving user: " + ex.getMessage());
                }
            }
        });

        dialog.setVisible(true);
    }

    private void showEditRoleDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
            return;
        }
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentRole = (String) tableModel.getValueAt(selectedRow, 2);

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Admin", "DataClerk"});
        roleCombo.setSelectedItem(currentRole);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("New Role:"));
        panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit User Role", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newRole = (String) roleCombo.getSelectedItem();
                userDBManager.updateUserRole(userId, newRole);
                loadUsers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating user role: " + ex.getMessage());
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDBManager.deleteUser(userId);
                loadUsers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
            }
        }
    }
}
