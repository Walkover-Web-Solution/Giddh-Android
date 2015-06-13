package com.Giddh.dtos;

import java.util.ArrayList;

/**
 * Created by walkover on 3/4/15.
 */
public class SummaryGroup {
    String groupName;
    Double closingBal;
    ArrayList<SummaryAccount> accounts;

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



    public ArrayList<SummaryAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<SummaryAccount> accounts) {
        this.accounts = accounts;
    }
}

