package com.Giddh.dtos;

import java.io.Serializable;

/**
 * Created by walkover on 3/4/15.
 */
public class SummaryAccount implements Serializable {
    String groupName;
    Double closingBal;
    String AccountName;
    String transactionType;
    String accountId;
    Boolean group;
    String entryId;
    String company_id;
    int eyId;
    String tripid;

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public int getEyId() {
        return eyId;
    }

    public void setEyId(int eyId) {
        this.eyId = eyId;
    }

    public Boolean getGroup() {
        return group;
    }

    public void setGroup(Boolean group) {
        this.group = group;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getClosingBal() {
        return closingBal;
    }

    public void setClosingBal(Double closingBal) {
        this.closingBal = closingBal;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

