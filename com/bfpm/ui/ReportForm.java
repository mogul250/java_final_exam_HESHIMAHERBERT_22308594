package com.bfpm.ui;

import com.bfpm.service.ReportService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class ReportForm extends JFrame {
    private ReportService reportService;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public ReportForm() {
        reportService = new ReportService();
        setTitle("Financial Reports");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JTextField startDateField = new JTextField("2023-01-01", 10);
        JTextField endDateField = new JTextField("2023-12-31", 10);
        JButton pnlButton = new JButton("Generate P&L Report");
        JButton expenseButton = new JButton("Generate Expense Trend Report");

        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        inputPanel.add(endDateField);
        inputPanel.add(pnlButton);
        inputPanel.add(expenseButton);

        String[] columns = {"Category", "Amount"};
        tableModel = new DefaultTableModel(columns, 0);
        reportTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        pnlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LocalDate start = LocalDate.parse(startDateField.getText());
                    LocalDate end = LocalDate.parse(endDateField.getText());
                    Map<String, BigDecimal> report = reportService.generatePnLReport(start, end);
                    tableModel.setRowCount(0);
                    for (Map.Entry<String, BigDecimal> entry : report.entrySet()) {
                        tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReportForm.this, "Error generating report: " + ex.getMessage());
                }
            }
        });

        expenseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LocalDate start = LocalDate.parse(startDateField.getText());
                    LocalDate end = LocalDate.parse(endDateField.getText());
                    Map<String, BigDecimal> report = reportService.generateExpenseTrendReport(start, end);
                    tableModel.setRowCount(0);
                    for (Map.Entry<String, BigDecimal> entry : report.entrySet()) {
                        tableModel.addRow(new Object[]{entry.getKey(), entry.getValue()});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReportForm.this, "Error generating report: " + ex.getMessage());
                }
            }
        });
    }
}
