package com.bfpm.model;

public class Account {
    private int accountId;
    private String accountName;
    private String accountType;

    public Account() {}

    public Account(int accountId, String accountName, String accountType) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountType = accountType;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return accountName;
    }
}
