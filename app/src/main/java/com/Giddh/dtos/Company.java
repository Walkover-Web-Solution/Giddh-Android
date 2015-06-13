package com.Giddh.dtos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by walkover on 7/3/15.
 */
public class Company implements Serializable{
    ArrayList<String> companyNames;
    String companyName;
    String companyId;
    String companyType;
    String emailId;
    String financialYear;

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public ArrayList<String> getCompanyNames() {
        return companyNames;
    }

    public void setCompanyNames(ArrayList<String> companyNames) {
        this.companyNames = companyNames;
    }


}
