package com.bfpm.service;

import com.bfpm.dbmanager.TransactionDBManager;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.bfpm.model.Transaction;
import com.bfpm.dbmanager.AccountDBManager;
import com.bfpm.model.Account;

public class ReportService {
    private TransactionDBManager transactionDBManager;
    private AccountDBManager accountDBManager;

    public ReportService() {
        transactionDBManager = new TransactionDBManager();
        accountDBManager = new AccountDBManager();
    }

    public Map<String, BigDecimal> generatePnLReport(LocalDate start, LocalDate end) throws SQLException {
        Map<String, BigDecimal> report = new HashMap<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        List<Transaction> transactions = transactionDBManager.getTransactionsByDateRange(start, end);
        Map<Integer, Account> accounts = new HashMap<>();
        for (Account acc : accountDBManager.getAllAccounts()) {
            accounts.put(acc.getAccountId(), acc);
        }

        for (Transaction t : transactions) {
            Account acc = accounts.get(t.getAccountId());
            if (acc != null) {
                if ("Revenue".equals(acc.getAccountType())) {
                    totalRevenue = totalRevenue.add(t.getAmount());
                } else if ("Expense".equals(acc.getAccountType())) {
                    totalExpense = totalExpense.add(t.getAmount());
                }
            }
        }

        BigDecimal netIncome = totalRevenue.subtract(totalExpense);
        report.put("Total Revenue", totalRevenue);
        report.put("Total Expense", totalExpense);
        report.put("Net Income", netIncome);
        return report;
    }

    public Map<String, BigDecimal> generateExpenseTrendReport(LocalDate start, LocalDate end) throws SQLException {
        Map<String, BigDecimal> report = new HashMap<>();
        List<Transaction> transactions = transactionDBManager.getTransactionsByDateRange(start, end);
        Map<Integer, Account> accounts = new HashMap<>();
        for (Account acc : accountDBManager.getAllAccounts()) {
            accounts.put(acc.getAccountId(), acc);
        }

        for (Transaction t : transactions) {
            Account acc = accounts.get(t.getAccountId());
            if (acc != null && "Expense".equals(acc.getAccountType())) {
                String accountName = acc.getAccountName();
                report.put(accountName, report.getOrDefault(accountName, BigDecimal.ZERO).add(t.getAmount()));
            }
        }
        return report;
    }
}
