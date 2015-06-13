package com.Giddh.dtos;

import android.util.SparseBooleanArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by walkover on 25/3/15.
 */
public class UserEmail implements Serializable, Comparable<UserEmail> {
    long id;
    String name;
    String email;
    String photoid;
    ArrayList<SparseBooleanArray> selected_mails;
    ArrayList<UserEmail> AllEmails;

    public ArrayList<UserEmail> getAllEmails() {
        return AllEmails;
    }

    public void setAllEmails(ArrayList<UserEmail> allEmails) {
        AllEmails = allEmails;
    }

    public ArrayList<SparseBooleanArray> getSelected_mails() {
        return selected_mails;
    }

    public void setSelected_mails(ArrayList<SparseBooleanArray> selected_mails) {
        this.selected_mails = selected_mails;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    @Override
    public int compareTo(UserEmail another) {
        int i = this.email.compareTo(another.email);
        return i;
    }
}



