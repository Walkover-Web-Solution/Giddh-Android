package com.Giddh.dtos;

import java.io.Serializable;

/**
 * Created by walkover on 7/3/15.
 */
public class userTransactions implements Serializable {
    String debit;
    String credit;
    String senderName;
    String date;
    String time;
    String amount;
    int id;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
