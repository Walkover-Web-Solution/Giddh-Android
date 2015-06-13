package com.Giddh.dtos;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by walkover on 12/3/15.
 */
public class CompanyDetails implements Serializable {
    ArrayList<GroupDetails> groupDetail;
    String debitTotal;
    String creditTotal;
    String closingBalance;
    String openingBalance;

    public String getDebitTotal() {
        return debitTotal;
    }

    public void setDebitTotal(String debitTotal) {
        this.debitTotal = debitTotal;
    }

    public String getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getCreditTotal() {
        return creditTotal;
    }

    public void setCreditTotal(String creditTotal) {
        this.creditTotal = creditTotal;
    }

    public ArrayList<GroupDetails> getGroupDetail() {
        return groupDetail;
    }

    public void setGroupDetail(ArrayList<GroupDetails> groupDetail) {
        this.groupDetail = groupDetail;
    }

    public String getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }









}

