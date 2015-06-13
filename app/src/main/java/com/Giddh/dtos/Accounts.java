package com.Giddh.dtos;

import java.io.Serializable;

/**
 * Created by walkover on 29/3/15.
 */
public class Accounts implements Serializable {
    int _id;
    String groupId;
    String AccountName;
    String Acc_webId;
    String uniqueName;
    String senderId;
    double openingBalance;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getAcc_webId() {
        return Acc_webId;
    }

    public void setAcc_webId(String acc_webId) {
        Acc_webId = acc_webId;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAccountName() {
        return AccountName;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }


}