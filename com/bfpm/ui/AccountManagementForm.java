package com.bfpm.ui;

import com.bfpm.model.Account;
import com.bfpm.dbmanager.AccountDBManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class AccountManagementForm extends JFrame {
    private AccountDBManager accountDBManager;
    private JTable accountTable;
    private DefaultTableModel tableModel;

    public AccountManagementForm() {
        accountDBManager = new AccountDBManager();
        setTitle("Manage Chart of Accounts");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadAccounts();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table for accounts
        String[] columns = {"ID", "Account Name", "Type"};
        tableModel = new DefaultTableModel(columns, 0);
        accountTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(accountTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Account");
        JButton editButton = new JButton("Edit Account");
        JButton deleteButton = new JButton("Delete Account");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddAccountDialog());
        editButton.addActionListener(e -> showEditAccountDialog());
        deleteButton.addActionListener(e -> deleteAccount());
        refreshButton.addActionListener(e -> loadAccounts());
    }

    private void loadAccounts() {
        try {
            tableModel.setRowCount(0);
            List<Account> accounts = accountDBManager.getAllAccounts();
            for (Account a : accounts) {
                tableModel.addRow(new Object[]{a.getAccountId(), a.getAccountName(), a.getAccountType()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage());
        }
    }

    private void showAddAccountDialog() {
        JDialog dialog = new JDialog(this, "Add Account", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Revenue", "Expense"});
        JButton saveButton = new JButton("Save");

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Account Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        dialog.add(typeCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        dialog.add(saveButton, gbc);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    String type = (String) typeCombo.getSelectedItem();
                    Account account = new Account(0, name, type);
                    accountDBManager.createAccount(account);
                    loadAccounts();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error saving account: " + ex.getMessage());
                }
            }
        });

        dialog.setVisible(true);
    }

    private void showEditAccountDialog() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account to edit.");
            return;
        }
        int accountId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentType = (String) tableModel.getValueAt(selectedRow, 2);

        JTextField nameField = new JTextField(currentName, 15);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Revenue", "Expense"});
        typeCombo.setSelectedItem(currentType);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Account Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Account", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText();
                String newType = (String) typeCombo.getSelectedItem();
                Account account = new Account(accountId, newName, newType);
                accountDBManager.updateAccount(account);
                loadAccounts();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating account: " + ex.getMessage());
            }
        }
    }

    private void deleteAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account to delete.");
            return;
        }
        int accountId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this account?");
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                accountDBManager.deleteAccount(accountId);
                loadAccounts();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting account: " + ex.getMessage());
            }
        }
    }
}
