package com.Giddh.dtos;

import java.util.ArrayList;

/**
 * Created by walkover on 6/4/15.
 */
public class SummaryEntry {
    String date;
    ArrayList<SummaryAccount> entries;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<SummaryAccount> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<SummaryAccount> entries) {
        this.entries = entries;
    }
}
