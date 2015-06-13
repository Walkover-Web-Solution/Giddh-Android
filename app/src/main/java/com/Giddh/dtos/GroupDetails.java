package com.Giddh.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by walkover on 13/3/15.
 */
public class GroupDetails implements Serializable {


    String groupName;
    String groupId;
    String openingBalance;
    String closingBalance;
    String belongsTo;
    String debitTotal;
    String creditTotal;
    ArrayList<GroupDetails> subGroupDetails;
    ArrayList<AccountDetails> accountDetails;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }

    public String getDebitTotal() {
        return debitTotal;
    }

    public void setDebitTotal(String debitTotal) {
        this.debitTotal = debitTotal;
    }

    public String getCreditTotal() {
        return creditTotal;
    }

    public void setCreditTotal(String creditTotal) {
        this.creditTotal = creditTotal;
    }

    public ArrayList<GroupDetails> getSubGroupDetails() {
        return subGroupDetails;
    }

    public void setSubGroupDetails(ArrayList<GroupDetails> subGroupDetails) {
        this.subGroupDetails = subGroupDetails;
    }

    public ArrayList<AccountDetails> getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(ArrayList<AccountDetails> accountDetails) {
        this.accountDetails = accountDetails;
    }


}