package com.Giddh.dtos;

import java.io.Serializable;

/**
 * Created by walkover on 13/3/15.
 */
public class AccountDetails implements Serializable {
    String accountName;
    String accountId;
    String openingBalance;
    String dbal;
    String balance;
    String cbal;
    String openingBalanceType;
    String uniqueName;

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getOpeningBalanceType() {
        return openingBalanceType;
    }

    public void setOpeningBalanceType(String openingBalanceType) {
        this.openingBalanceType = openingBalanceType;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getDbal() {
        return dbal;
    }

    public void setDbal(String dbal) {
        this.dbal = dbal;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCbal() {
        return cbal;
    }

    public void setCbal(String cbal) {
        this.cbal = cbal;
    }


}
