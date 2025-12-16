package com.bfpm.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private int accountId;
    private int recordedByUserId;

    public Transaction() {}

    public Transaction(int transactionId, LocalDate date, String description, BigDecimal amount, int accountId, int recordedByUserId) {
        this.transactionId = transactionId;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.accountId = accountId;
        this.recordedByUserId = recordedByUserId;
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getRecordedByUserId() {
        return recordedByUserId;
    }

    public void setRecordedByUserId(int recordedByUserId) {
        this.recordedByUserId = recordedByUserId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", accountId=" + accountId +
                ", recordedByUserId=" + recordedByUserId +
                '}';
    }
}
