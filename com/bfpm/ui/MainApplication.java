package com.bfpm.ui;

import com.bfpm.model.User;
import com.bfpm.dbmanager.UserDBManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class MainApplication extends JFrame {
    private User currentUser;
    private UserDBManager userDBManager;

    public MainApplication() {
        userDBManager = new UserDBManager();
        setTitle("Finance Platform Manager");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showLoginForm();
    }

    private void showLoginForm() {
        getContentPane().removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                try {
                    currentUser = userDBManager.authenticate(username, password);
                    if (currentUser != null) {
                        showMainMenu();
                    } else {
                        JOptionPane.showMessageDialog(MainApplication.this, "Invalid credentials");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(MainApplication.this, "Database error: " + ex.getMessage());
                }
            }
        });

        revalidate();
        repaint();
    }

    private void showMainMenu() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        // Transactions Tab
        TransactionForm transactionForm = new TransactionForm(currentUser);
        tabbedPane.addTab("Transactions", transactionForm.getContentPane());

        // Reports Tab
        ReportForm reportForm = new ReportForm();
        tabbedPane.addTab("Reports", reportForm.getContentPane());

        if ("Admin".equals(currentUser.getUserRole())) {
            // Users Tab
            UserManagementForm userForm = new UserManagementForm();
            tabbedPane.addTab("Users", userForm.getContentPane());

            // Accounts Tab
            AccountManagementForm accountForm = new AccountManagementForm();
            tabbedPane.addTab("Accounts", accountForm.getContentPane());
        }

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> showLoginForm());

        add(tabbedPane, BorderLayout.CENTER);
        add(logoutButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApplication().setVisible(true));
    }
}
