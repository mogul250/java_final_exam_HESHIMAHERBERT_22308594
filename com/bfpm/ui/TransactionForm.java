package com.bfpm.ui;

import com.bfpm.model.User;
import com.bfpm.model.Account;
import com.bfpm.model.Transaction;
import com.bfpm.dbmanager.TransactionDBManager;
import com.bfpm.dbmanager.AccountDBManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TransactionForm extends JFrame {
    private User currentUser;
    private TransactionDBManager transactionDBManager;
    private AccountDBManager accountDBManager;
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public TransactionForm(User currentUser) {
        this.currentUser = currentUser;
        transactionDBManager = new TransactionDBManager();
        accountDBManager = new AccountDBManager();

        setTitle("Manage Transactions");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        loadTransactions();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table for transactions
        String[] columns = {"ID", "Date", "Description", "Amount", "Account"};
        tableModel = new DefaultTableModel(columns, 0);
        transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add/Edit Panel
        JPanel addPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField dateField = new JTextField(10);
        JTextField descField = new JTextField(20);
        JTextField amountField = new JTextField(10);
        JComboBox<Account> accountCombo = new JComboBox<>();
        try {
            List<Account> accounts = accountDBManager.getAllAccounts();
            for (Account acc : accounts) {
                accountCombo.addItem(acc);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage());
        }
        JButton saveButton = new JButton("Add Transaction");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        if ("DataClerk".equals(currentUser.getUserRole())) {
            deleteButton.setEnabled(false);
        }

        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        addPanel.add(dateField, gbc);
        gbc.gridx = 2;
        addPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 3;
        addPanel.add(descField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        addPanel.add(amountField, gbc);
        gbc.gridx = 2;
        addPanel.add(new JLabel("Account:"), gbc);
        gbc.gridx = 3;
        addPanel.add(accountCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        addPanel.add(saveButton, gbc);
        gbc.gridx = 2;
        addPanel.add(editButton, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        addPanel.add(deleteButton, gbc);
        gbc.gridx = 2;
        addPanel.add(refreshButton, gbc);

        add(addPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> addTransaction(dateField, descField, amountField, accountCombo));
        editButton.addActionListener(e -> editTransaction(dateField, descField, amountField, accountCombo, saveButton));
        deleteButton.addActionListener(e -> deleteTransaction());
        refreshButton.addActionListener(e -> loadTransactions());
    }

    private void loadTransactions() {
        try {
            tableModel.setRowCount(0);
            List<Transaction> transactions;
            if ("Admin".equals(currentUser.getUserRole())) {
                transactions = transactionDBManager.getAllTransactions();
            } else {
                transactions = transactionDBManager.getTransactionsByUser(currentUser.getUserId());
            }
            for (Transaction t : transactions) {
                Account account = accountDBManager.getAccountById(t.getAccountId());
                tableModel.addRow(new Object[]{t.getTransactionId(), t.getDate(), t.getDescription(),
                                               t.getAmount(), account != null ? account.getAccountName() : "Unknown"});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + ex.getMessage());
        }
    }

    private void addTransaction(JTextField dateField, JTextField descField, JTextField amountField, JComboBox<Account> accountCombo) {
        try {
            LocalDate date = LocalDate.parse(dateField.getText());
            String desc = descField.getText();
            BigDecimal amount = new BigDecimal(amountField.getText());
            Account selectedAccount = (Account) accountCombo.getSelectedItem();
            Transaction transaction = new Transaction(0, date, desc, amount, selectedAccount.getAccountId(), currentUser.getUserId());
            transactionDBManager.saveTransaction(transaction);
            loadTransactions();
            dateField.setText("");
            descField.setText("");
            amountField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving transaction: " + ex.getMessage());
        }
    }

    private void editTransaction(JTextField dateField, JTextField descField, JTextField amountField, JComboBox<Account> accountCombo, JButton saveButton) {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.");
            return;
        }
        int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Transaction transaction = transactionDBManager.getTransactionById(transactionId);
            if (transaction == null || (!"Admin".equals(currentUser.getUserRole()) && transaction.getRecordedByUserId() != currentUser.getUserId())) {
                JOptionPane.showMessageDialog(this, "You cannot edit this transaction.");
                return;
            }
            dateField.setText(transaction.getDate().toString());
            descField.setText(transaction.getDescription());
            amountField.setText(transaction.getAmount().toString());
            Account account = accountDBManager.getAccountById(transaction.getAccountId());
            accountCombo.setSelectedItem(account);
            saveButton.setText("Update Transaction");
            saveButton.removeActionListener(saveButton.getActionListeners()[0]);
            saveButton.addActionListener(e -> updateTransaction(transactionId, dateField, descField, amountField, accountCombo, saveButton));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error editing transaction: " + ex.getMessage());
        }
    }

    private void updateTransaction(int transactionId, JTextField dateField, JTextField descField, JTextField amountField, JComboBox<Account> accountCombo, JButton saveButton) {
        try {
            LocalDate date = LocalDate.parse(dateField.getText());
            String desc = descField.getText();
            BigDecimal amount = new BigDecimal(amountField.getText());
            Account selectedAccount = (Account) accountCombo.getSelectedItem();
            Transaction transaction = new Transaction(transactionId, date, desc, amount, selectedAccount.getAccountId(), currentUser.getUserId());
            transactionDBManager.updateTransaction(transaction);
            loadTransactions();
            dateField.setText("");
            descField.setText("");
            amountField.setText("");
            saveButton.setText("Add Transaction");
            saveButton.removeActionListener(saveButton.getActionListeners()[0]);
            saveButton.addActionListener(e -> addTransaction(dateField, descField, amountField, accountCombo));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating transaction: " + ex.getMessage());
        }
    }

    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog(this, "Add Transaction", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField dateField = new JTextField(10);
        JTextField descField = new JTextField(20);
        JTextField amountField = new JTextField(10);
        JComboBox<Account> accountCombo = new JComboBox<>();
        try {
            List<Account> accounts = accountDBManager.getAllAccounts();
            for (Account acc : accounts) {
                accountCombo.addItem(acc);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage());
            return;
        }
        JButton saveButton = new JButton("Save");

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dialog.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        dialog.add(descField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        dialog.add(amountField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Account:"), gbc);
        gbc.gridx = 1;
        dialog.add(accountCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(saveButton, gbc);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LocalDate date = LocalDate.parse(dateField.getText());
                    String desc = descField.getText();
                    BigDecimal amount = new BigDecimal(amountField.getText());
                    Account selectedAccount = (Account) accountCombo.getSelectedItem();
                    Transaction transaction = new Transaction(0, date, desc, amount, selectedAccount.getAccountId(), currentUser.getUserId());
                    transactionDBManager.saveTransaction(transaction);
                    loadTransactions();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error saving transaction: " + ex.getMessage());
                }
            }
        });

        dialog.setVisible(true);
    }

    private void showEditTransactionDialog() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.");
            return;
        }
        int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Transaction transaction = transactionDBManager.getTransactionById(transactionId);
            if (transaction == null || (!"Admin".equals(currentUser.getUserRole()) && transaction.getRecordedByUserId() != currentUser.getUserId())) {
                JOptionPane.showMessageDialog(this, "You cannot edit this transaction.");
                return;
            }
            // Similar to add dialog, but pre-fill fields
            showAddTransactionDialog(); // For simplicity, reuse add dialog; in real app, pre-fill
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error editing transaction: " + ex.getMessage());
        }
    }

    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.");
            return;
        }
        int transactionId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            Transaction transaction = transactionDBManager.getTransactionById(transactionId);
            if (transaction == null || (!"Admin".equals(currentUser.getUserRole()) && transaction.getRecordedByUserId() != currentUser.getUserId())) {
                JOptionPane.showMessageDialog(this, "You cannot delete this transaction.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?");
            if (confirm == JOptionPane.YES_OPTION) {
                transactionDBManager.deleteTransaction(transactionId);
                loadTransactions();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting transaction: " + ex.getMessage());
        }
    }
}
