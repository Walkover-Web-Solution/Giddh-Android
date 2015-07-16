package com.Giddh.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.Giddh.commonUtilities.Prefs;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.Company;
import com.Giddh.dtos.DeletedEntry;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.GroupInfo;
import com.Giddh.dtos.SmsDto;
import com.Giddh.dtos.SummaryAccount;
import com.Giddh.dtos.SummaryEntry;
import com.Giddh.dtos.TripInfo;
import com.Giddh.dtos.TripShare;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserService {
    public static String Lock = "dblock";
    static Context context;
    static DBHandler dbH;
    static UserService userservice;
    SQLiteDatabase sdb;

    private UserService(Context context) {
        UserService.context = context;
    }

    public static synchronized void extraFunction() {
        PackageInfo packinfo;
        int version = 0;
        try {
            packinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (dbH == null) {
            dbH = new DBHandler(context, null, null, version);
        }
    }

    public static UserService getUserServiceInstance(Context c) {
        if (userservice == null)
            userservice = new UserService(c);
        return userservice;
    }

    //add entry in db
    public long addentrydata(EntryInfo dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.EY_AMOUNT, dto.getAmount());
        values.put(DBHandler.EY_COMPANY_ID, dto.getCompanyId());
        values.put(DBHandler.EY_CREADITACCOUNT, dto.getCreditAccount());
        values.put(DBHandler.EY_DEBITACCOUNT, dto.getDebitAccount());
        values.put(DBHandler.EY_EMAILLOAN, dto.getEmailloan());
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        values.put(DBHandler.EY_DATE, newString);
        values.put(DBHandler.EY_DESCRIPTION, dto.getDescription());
        values.put(DBHandler.EY_TRIPID, dto.getTripId());
        values.put(DBHandler.EY_TRANSACTIONTYPE, dto.getTransactionType());
        values.put(DBHandler.EY_GROUP_ID, dto.getGroupId());
        values.put(DBHandler.EY_ENTRYID, dto.getEntryId());
        long i = sdb.insert(DBHandler.ENTRY_INFO, null, values);
        Log.d("Insertion in database", "Insertion database " + i + "" + dto.getAmount());
        Log.e("settingAmount", "" + dto.getAmount());
        return i;
    }

    //add entry in db
    public long addsms(SmsDto dto) {
        long i = 0l;
        if (checksms(dto.getSmsId()).size() == 0) {
            Log.d("Insertion in database", "Insertion database ");
            extraFunction();
            sdb = dbH.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHandler.SMSID, dto.getAmount());
            values.put(DBHandler.PARSESTATUS, dto.getParsed());
            i = sdb.insert(DBHandler.SMS_DATA, null, values);
            Log.d("Insertion in database", "Insertion database " + i + "" + dto.getAmount());
            Log.e("settingsms", "" + dto.getAmount());
        } else {
            updatesms(dto);
        }
        return i;
    }

    //add entry in db
    public long updatesms(SmsDto dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.SMSID, dto.getAmount());
        values.put(DBHandler.PARSESTATUS, dto.getParsed());
        long i = sdb.update(DBHandler.SMS_DATA, values, " sms_id= '" + dto.getSmsId() + "'", null);
        Log.d("Insertion in database", "Insertion database " + i + "" + dto.getSmsId());
        Log.e("settingsms", "" + dto.getAmount());
        return i;
    }

    //get count acc and card
    public ArrayList<SmsDto> checksms(String smsid) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getReadableDatabase();
        ArrayList<SmsDto> smsdata = new ArrayList<>();
        String s;
        int sum;
        s = "SELECT *  FROM " + DBHandler.SMS_DATA + " where sms_id='" + smsid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                SmsDto acdto;
                do {
                    acdto = new SmsDto();
                    acdto.setSmsId(c.getString(c.getColumnIndex(DBHandler.SMSID)));
                    acdto.setParsed(c.getString(c.getColumnIndex(DBHandler.PARSESTATUS)));
                    smsdata.add(acdto);
                } while (c.moveToNext());
            }
            c.close();
        }
        return smsdata;
    }

    //update entry in db
    public long update_entry(EntryInfo dto, int idval) {
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.EY_AMOUNT, dto.getAmount());
        values.put(DBHandler.EY_CREADITACCOUNT, dto.getCreditAccount());
        values.put(DBHandler.EY_DEBITACCOUNT, dto.getDebitAccount());
        values.put(DBHandler.EY_ENTRYID, dto.getEntryId());
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        values.put(DBHandler.EY_DATE, newString);
        values.put(DBHandler.EY_DESCRIPTION, dto.getDescription());
        values.put(DBHandler.EY_TRIPID, dto.getTripId());
        long i = sdb.update(DBHandler.ENTRY_INFO, values, " _id= '" + idval + "'", null);
        Log.d("update in database", "update " + i + "" + dto.getAmount());
        Log.e("settingAmount", "" + dto.getAmount());
        return i;
    }

    //update entry in db
    public long update_entry_trip(EntryInfo dto) {
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.TEY_AMOUNT, dto.getAmount());
        values.put(DBHandler.TEY_CREDITACCOUNT, dto.getCreditAccount());
        values.put(DBHandler.TEY_DEBITACCOUNT, dto.getDebitAccount());
        values.put(DBHandler.TEY_ENTRYID, dto.getEntryId());
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        values.put(DBHandler.TEY_DATE, newString);
        values.put(DBHandler.TEY_DESCRIPTION, dto.getDescription());
        long i = sdb.update(DBHandler.TRIP_ENTRY_INFO, values, " tripID= '" + dto.getTripId() + "' AND company_id='" + dto.getCompanyId() + "' AND entryId='" + dto.getEntryId() + "'", null);
        Log.d("update in database", "update " + i + "" + dto.getAmount());
        return i;
    }

    //add entry in db
    public long addTripentrydata(EntryInfo dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.TEY_AMOUNT, dto.getAmount());
        values.put(DBHandler.TEY_COMPANY_ID, dto.getCompanyId());
        values.put(DBHandler.TEY_CREDITACCOUNT, dto.getCreditAccount());
        values.put(DBHandler.TEY_DEBITACCOUNT, dto.getDebitAccount());
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dto.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String newString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        values.put(DBHandler.TEY_DATE, newString);
        values.put(DBHandler.TEY_DESCRIPTION, dto.getDescription());
        values.put(DBHandler.TEY_TRIPID, dto.getTripId());
        values.put(DBHandler.TEY_TRANSACTIONTYPE, dto.getTransactionType());
        values.put(DBHandler.TEY_GROUP_ID, dto.getGroupId());
        values.put(DBHandler.TEY_ENTRYID, dto.getEntryId());
        values.put(DBHandler.TEY_EMAIL, dto.getEmail());
        long i = sdb.insert(DBHandler.TRIP_ENTRY_INFO, null, values);
        Log.d("Insertion in database", "Insertion database " + i + "" + dto.getAmount());
        return i;
    }

    //getting all names giv/receiving
    public ArrayList<Accounts> getallAccounts(String groupId, Boolean includeOther, Boolean all) {
        System.out.println("starting fetch");
        ArrayList<Accounts> account_names = new ArrayList<Accounts>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        if (includeOther)
            s = "SELECT *  FROM " + DBHandler.ACCOUNTS + " where group_id=" + groupId +
                    " OR (group_id=2 AND name='Loan') OR group_id=4 ORDER BY name ASC";
        else if (all)
            s = "SELECT *  FROM " + DBHandler.ACCOUNTS;
        else
            s = "SELECT *  FROM " + DBHandler.ACCOUNTS + " where group_id=" + groupId +
                    " OR (group_id=2 AND name != 'Loan')";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                Accounts acdto;
                do {
                    acdto = new Accounts();
                    acdto.set_id(c.getInt(c.getColumnIndex(DBHandler.ACCOUNT_ID)));
                    acdto.setGroupId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_GROUP_ID)));
                    acdto.setAccountName(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_NAME)));
                    acdto.setAcc_webId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_WEB_ID)));
                    acdto.setOpeningBalance(c.getDouble(c.getColumnIndex(DBHandler.ACCOUNT_OPENING_BAL)));
                    account_names.add(acdto);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return account_names;
    }

    //getting all names giv/receiving
    public ArrayList<Accounts> getaccounts(String groupId) {
        System.out.println("starting fetch");
        ArrayList<Accounts> account_names = new ArrayList<Accounts>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT *  FROM " + DBHandler.ACCOUNTS + " where group_id=" + groupId;
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                Accounts acdto;
                do {
                    acdto = new Accounts();
                    acdto.set_id(c.getInt(c.getColumnIndex(DBHandler.ACCOUNT_ID)));
                    acdto.setGroupId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_GROUP_ID)));
                    acdto.setAccountName(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_NAME)));
                    acdto.setAcc_webId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_WEB_ID)));
                    acdto.setOpeningBalance(c.getDouble(c.getColumnIndex(DBHandler.ACCOUNT_OPENING_BAL)));
                    account_names.add(acdto);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return account_names;
    }

    //getting all names giv/receiving
    public Accounts getinfoExpacc(String accName, String groupId) {
        Accounts acdto = null;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT *  FROM " + DBHandler.ACCOUNTS + " where name='" + accName + "' AND group_id= '" + groupId + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst()) {
            do {
                acdto = new Accounts();
                acdto.set_id(c.getInt(c.getColumnIndex(DBHandler.ACCOUNT_ID)));
                acdto.setGroupId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_GROUP_ID)));
                acdto.setAccountName(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_NAME)));
                acdto.setAcc_webId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_WEB_ID)));
                acdto.setOpeningBalance(c.getDouble(c.getColumnIndex(DBHandler.ACCOUNT_OPENING_BAL)));
            } while (c.moveToNext());
        }
        c.close();
        System.out.println("stoping fetch");
        return acdto;
    }

    //getting count
    public int getcount(String columnName) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        int sum = 0;
        s = "SELECT *  FROM " + columnName;
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            sum = c.getCount();
        System.out.println("stoping fetch");
        return sum;
    }

    //get count acc and card
    public ArrayList<Accounts> getcountacc(String groupid, String accountname) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getReadableDatabase();
        ArrayList<Accounts> accounts = new ArrayList<>();
        String s;
        int sum;
        s = "SELECT *  FROM " + DBHandler.ACCOUNTS + " where name!='" + accountname + "' AND group_id='" + groupid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                Accounts acdto;
                do {
                    acdto = new Accounts();
                    acdto.set_id(c.getInt(c.getColumnIndex(DBHandler.ACCOUNT_ID)));
                    acdto.setGroupId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_GROUP_ID)));
                    acdto.setAccountName(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_NAME)));
                    acdto.setAcc_webId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_WEB_ID)));
                    acdto.setOpeningBalance(c.getDouble(c.getColumnIndex(DBHandler.ACCOUNT_OPENING_BAL)));
                    accounts.add(acdto);
                } while (c.moveToNext());
            }
            c.close();
        }
        return accounts;
    }

    //get companie list
    public ArrayList<Company> getcompaniesList(String emailid) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getReadableDatabase();
        ArrayList<Company> company = new ArrayList<>();
        String s;
        int sum;
        s = "SELECT *  FROM " + DBHandler.COMPANYLIST + " where emailId='" + emailid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                Company acdto;
                do {
                    acdto = new Company();
                    acdto.setCompanyName(c.getString(c.getColumnIndex(DBHandler.COMPANY_NAME_)));
                    acdto.setCompanyType(c.getString(c.getColumnIndex(DBHandler.COMPANY_TYPE)));
                    acdto.setEmailId(c.getString(c.getColumnIndex(DBHandler.COMPANY_EMAILID)));
                    acdto.setCompanyId(c.getString(c.getColumnIndex(DBHandler.COMPANY_ID)));
                    acdto.setFinancialYear(c.getString(c.getColumnIndex(DBHandler.FINANCIALYEAR)));
                    company.add(acdto);
                } while (c.moveToNext());
            }
            c.close();
        }
        return company;
    }

    //getting max acc_id
    public int getmaxaccId() {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        String s;
        int sum;
        s = "SELECT MAX (account_id)  FROM " + DBHandler.ACCOUNTS;
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            sum = Integer.parseInt(c.getString(0));
        else
            sum = Integer.parseInt(String.valueOf(-1));
        System.out.println("stoping fetch");
        return sum;
    }

    //get account object for name/id
    public Accounts getaccountnameorId(String groupNameId) {
        System.out.println("starting fetch");
        Accounts acdto = null;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT *  FROM " + DBHandler.ACCOUNTS + " where account_id='" + groupNameId + "' OR name LIKE '%" + groupNameId + "%'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    acdto = new Accounts();
                    acdto.set_id(c.getInt(c.getColumnIndex(DBHandler.ACCOUNT_ID)));
                    acdto.setGroupId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_GROUP_ID)));
                    acdto.setAccountName(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_NAME)));
                    acdto.setAcc_webId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_WEB_ID)));
                    acdto.setOpeningBalance(c.getDouble(c.getColumnIndex(DBHandler.ACCOUNT_OPENING_BAL)));
                    acdto.setUniqueName(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_UNIQUENAME)));
                } while (c.moveToNext());
            } else {
                Accounts acdto1 = new Accounts();
                acdto1.setAccountName(groupNameId);
                acdto1.setOpeningBalance(0);
                int id = getmaxaccId() + 1;
                acdto1.setAcc_webId(String.valueOf(id));
                acdto1.setGroupId("1");
                addaccountsdata(acdto1);
                getaccountnameorId(groupNameId);
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return acdto;
    }

    //getting groupInfo
    public ArrayList<GroupInfo> getgroupInfo() {
        System.out.println("starting fetch");
        ArrayList<GroupInfo> group_names = new ArrayList<GroupInfo>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT *  FROM " + DBHandler.GROUP_INFO;
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                GroupInfo gdto;
                do {
                    gdto = new GroupInfo();
                    gdto.setParentId(c.getString(c.getColumnIndex(DBHandler.GRP_PARENT_ID)));
                    gdto.setSystemId(c.getString(c.getColumnIndex(DBHandler.GRP_SYSTEM_ID)));
                    gdto.setGroupName(c.getString(c.getColumnIndex(DBHandler.GRP_NAME)));
                    group_names.add(gdto);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return group_names;
    }

    //getting all trips_share
    public ArrayList<TripShare> getallshareTrips(String tripId, Boolean getAll, String cid, Boolean noadd) {
        System.out.println("starting fetch");
        ArrayList<TripShare> trip_names = new ArrayList<TripShare>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        if (getAll) {
            s = "SELECT *  FROM " + DBHandler.TRIP_SHARE + " where trip_id= '" + tripId + "' ";
        } else {
            s = "SELECT *  FROM " + DBHandler.TRIP_SHARE + " where trip_id= '" + tripId + "' AND companyId='" + cid + "'";
        }
        Cursor c = sdb.rawQuery(s, null);
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                TripShare dto;
                do {
                    dto = new TripShare();
                    dto.setEmail(c.getString(c.getColumnIndex(DBHandler.TS_EMAIL)));
                    dto.setOwner(c.getString(c.getColumnIndex(DBHandler.TS_OWNER)));
                    dto.setTripId(c.getString(c.getColumnIndex(DBHandler.TS_TRIP_ID)));
                    dto.setCompanyName(c.getString(c.getColumnIndex(DBHandler.TS_COMPANYNAME)));
                    dto.setCompanyId(c.getString(c.getColumnIndex(DBHandler.TS_COMPANYID)));
                    dto.setCompanyType(c.getString(c.getColumnIndex(DBHandler.TS_COMPANYNTYPE)));
                    if (noadd) {
                        if (!dto.getCompanyType().equals("2"))
                            trip_names.add(dto);
                    } else {
                        trip_names.add(dto);
                    }
                } while (c.moveToNext());
            }
            c.close();
        }
        if (getAll)
            Log.e("size of list", "" + trip_names.size());
        System.out.println("stoping fetch");
        return trip_names;
    }

    //getting all trips_share count
    public int getallshareTripscount(String tripId) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT count(*) FROM " + DBHandler.TRIP_SHARE + " where trip_id= '" + tripId + "' ";
        Cursor c = sdb.rawQuery(s, null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();
        System.out.println("stoping fetch");
        return count;
    }

    //getting entry Info
    public ArrayList<SummaryEntry> getEntryInfo(String accName) {
        System.out.println("starting fetch");
        ArrayList<SummaryAccount> summaryAcc;
        ArrayList<SummaryEntry> entry = new ArrayList<>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT DISTINCT date  FROM " + DBHandler.ENTRY_INFO + " where debit_account=" + accName + " OR creadit_account='" + accName + "'ORDER BY date DESC";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    summaryAcc = new ArrayList<SummaryAccount>();
                    SummaryEntry dto1 = new SummaryEntry();
                    dto1.setDate(c.getString(c.getColumnIndex(DBHandler.EY_DATE)));
                    String s1 = "SELECT *  FROM " + DBHandler.ENTRY_INFO + " where (debit_account='" + accName +
                            "' OR creadit_account='" + accName + "'" + ") AND date='" + c.getString(c.getColumnIndex(DBHandler.EY_DATE)) + "'";
                    Cursor c1 = sdb.rawQuery(s1, null);
                    if (c1 != null) {
                        if (c1.moveToFirst()) {
                            do {
                                SummaryAccount dto = new SummaryAccount();
                                dto.setClosingBal(c1.getDouble(c1.getColumnIndex(DBHandler.EY_AMOUNT)));
                         /*       dto.setClosingBal((Double.valueOf(c1.getColumnIndex(DBHandler.EY_AMOUNT))));*/
                                if (c1.getString(c1.getColumnIndex(DBHandler.EY_CREADITACCOUNT)).equals(accName)) {
                                    dto.setAccountId(c1.getString(c1.getColumnIndex(DBHandler.EY_DEBITACCOUNT)));
                                } else {
                                    dto.setAccountId(c1.getString(c1.getColumnIndex(DBHandler.EY_CREADITACCOUNT)));
                                }
                                dto.setTransactionType(c1.getString(c1.getColumnIndex(DBHandler.EY_TRANSACTIONTYPE)));
                                dto.setEyId(c1.getInt(c1.getColumnIndex(DBHandler.EY_ID)));
                                dto.setCompany_id(c1.getString(c1.getColumnIndex(DBHandler.COMPANY_ID)));
                                dto.setTripid(c1.getString(c1.getColumnIndex(DBHandler.EY_TRIPID)));
                                dto.setEntryId(c1.getString(c1.getColumnIndex(DBHandler.EY_ENTRYID)));
                                summaryAcc.add(dto);
                                Log.e("GettingAmount", "" + dto.getClosingBal());
                            } while (c1.moveToNext());
                        }
                        c1.close();
                    }
                    dto1.setEntries(summaryAcc);
                    entry.add(dto1);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return entry;
    }

    //getting entry Info income/expence
    public ArrayList<SummaryEntry> getEntryInfoIncomeExpense(String accName) {
        System.out.println("starting fetch");
        ArrayList<SummaryAccount> summaryAcc;
        ArrayList<SummaryEntry> entry = new ArrayList<>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT DISTINCT date  FROM " + DBHandler.ENTRY_INFO + " where transactionType='" + accName + "'ORDER BY date DESC";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    summaryAcc = new ArrayList<SummaryAccount>();
                    SummaryEntry dto1 = new SummaryEntry();
                    dto1.setDate(c.getString(c.getColumnIndex(DBHandler.EY_DATE)));
                    String s1 = "SELECT *  FROM " + DBHandler.ENTRY_INFO + " where date='" + c.getString(c.getColumnIndex(DBHandler.EY_DATE)) + "' AND transactionType='" + accName + "'";
                    Cursor c1 = sdb.rawQuery(s1, null);
                    if (c1 != null) {
                        if (c1.moveToFirst()) {
                            do {
                                SummaryAccount dto = new SummaryAccount();
                                dto.setClosingBal(c1.getDouble(c1.getColumnIndex(DBHandler.EY_AMOUNT)));
                                if (accName.equals("0")) {
                                    dto.setAccountId(c1.getString(c1.getColumnIndex(DBHandler.EY_CREADITACCOUNT)));
                                } else {
                                    dto.setAccountId(c1.getString(c1.getColumnIndex(DBHandler.EY_DEBITACCOUNT)));
                                }
                                dto.setTransactionType(c1.getString(c1.getColumnIndex(DBHandler.EY_TRANSACTIONTYPE)));
                                dto.setTripid(c1.getString(c1.getColumnIndex(DBHandler.EY_TRIPID)));
                                dto.setEyId(c1.getInt(c1.getColumnIndex(DBHandler.EY_ID)));
                                dto.setCompany_id(c1.getString(c1.getColumnIndex(DBHandler.COMPANY_ID)));
                                dto.setEntryId(c1.getString(c1.getColumnIndex(DBHandler.EY_ENTRYID)));
                                summaryAcc.add(dto);
                            } while (c1.moveToNext());
                        }
                        c1.close();
                    }
                    dto1.setEntries(summaryAcc);
                    entry.add(dto1);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return entry;
    }

    //getting trip Info
    public ArrayList<SummaryEntry> getTripInfo(String tripid, String cid) {
        System.out.println("starting fetch");
        ArrayList<SummaryAccount> summaryAcc;
        ArrayList<SummaryEntry> entry = new ArrayList<>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT DISTINCT date  FROM " + DBHandler.TRIP_ENTRY_INFO + " where tripID=" + tripid + " AND company_id='" + cid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    summaryAcc = new ArrayList<SummaryAccount>();
                    SummaryEntry dto1 = new SummaryEntry();
                    dto1.setDate(c.getString(c.getColumnIndex(DBHandler.EY_DATE)));
                    String s1 = "SELECT *  FROM " + DBHandler.TRIP_ENTRY_INFO + " where date='" + c.getString(c.getColumnIndex(DBHandler.EY_DATE)) + "' AND tripID=" + tripid + " AND company_id='" + cid + "'";
                    Cursor c1 = sdb.rawQuery(s1, null);
                    if (c1 != null) {
                        if (c1.moveToFirst()) {
                            do {
                                SummaryAccount dto = new SummaryAccount();
                                dto.setClosingBal(c1.getDouble(c1.getColumnIndex(DBHandler.EY_AMOUNT)));
                                if (c1.getString(c1.getColumnIndex(DBHandler.TEY_TRANSACTIONTYPE)).equals("1")) {
                                    if (c1.getString(c1.getColumnIndex(DBHandler.TEY_EMAIL)).equals(Prefs.getEmailId(context))) {
                                        dto.setAccountName(userservice.getaccountnameorId(c1.getString(c1.getColumnIndex(DBHandler.TEY_DEBITACCOUNT))).getAccountName());
                                    } else
                                        dto.setAccountName(c1.getString(c1.getColumnIndex(DBHandler.TEY_DEBITACCOUNT)));
                                } else {
                                    if (c1.getString(c1.getColumnIndex(DBHandler.TEY_EMAIL)).equals(Prefs.getEmailId(context)))
                                        dto.setAccountName(userservice.getaccountnameorId(c1.getString(c1.getColumnIndex(DBHandler.TEY_CREDITACCOUNT))).getAccountName());
                                    else {
                                        dto.setAccountName((c1.getString(c1.getColumnIndex(DBHandler.TEY_CREDITACCOUNT))));
                                    }
                                }
                                dto.setTransactionType(c1.getString(c1.getColumnIndex(DBHandler.TEY_TRANSACTIONTYPE)));
                                summaryAcc.add(dto);
                            } while (c1.moveToNext());
                        }
                        c1.close();
                    }
                    dto1.setEntries(summaryAcc);
                    entry.add(dto1);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return entry;
    }

    //getting entry Info
    public Double getSumExpenceIncomeEntry(String colomnName, String rowName) {
        System.out.println("starting fetch");
        Double sum = 0D;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT sum (amount)  FROM " + DBHandler.ENTRY_INFO + " where " + colomnName + "=" + rowName;
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            sum = c.getDouble(0);
        else
            sum = Double.valueOf(Long.valueOf(-1));
        System.out.println("stoping fetch");
        return sum;
    }

    //getting entry Info
    public Double getperheadcontri(String tripid, String date, Boolean total, Boolean transtype) {
        System.out.println("starting fetch");
        Double sum = 0D;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        if (total && transtype)
            s = "SELECT sum (amount)  FROM " + DBHandler.TRIP_ENTRY_INFO + " where tripID='" + tripid + "'AND transactionType='" + 1 + "'";
        else if (total && !transtype)
            s = "SELECT sum (amount)  FROM " + DBHandler.TRIP_ENTRY_INFO + " where tripID='" + tripid + "'AND transactionType='" + 0 + "'";
        else
            s = "SELECT sum (amount)  FROM " + DBHandler.TRIP_ENTRY_INFO + " where tripID='" + tripid + "' AND date='" + date + "'AND transactionType='" + 1 + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            sum = c.getDouble(0);
        /*else
            sum = Double.valueOf(-1);*/
        System.out.println("stoping fetch");
        return sum;
    }

    //getting openingbal
    public String getopening_balance(String accountname) {
        System.out.println("starting fetch");
        String name;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT opening_bal  FROM " + DBHandler.ACCOUNTS + " where name='" + accountname + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            name = c.getString(0);
        else
            name = String.valueOf(-1);
        System.out.println("stoping fetch");
        return name;
    }

    //getting closingbal
    public Double getclosing_bal(String accountname, Boolean giv) {
        System.out.println("starting fetch");
        Double name;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        if (giv)
            s = "SELECT sum (amount)  FROM " + DBHandler.ENTRY_INFO + " where (debit_account='" + accountname +
                    "' OR creadit_account='" + accountname + "'" + ") AND transactionType='" + 1 + "'";
        else
            s = "SELECT sum (amount)  FROM " + DBHandler.ENTRY_INFO + " where (debit_account='" + accountname +
                    "' OR creadit_account='" + accountname + "'" + ") AND transactionType='" + 0 + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            name = c.getDouble(0);
        else
            name = Double.valueOf(-1);
        System.out.println("stoping fetch");
        return name;
    }

    // Max entry id in trip
    public String getEntryIdtrip(String tripid) {
        System.out.println("starting fetch");
        String sum;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT MAX(entryId)  FROM " + DBHandler.TRIP_ENTRY_INFO + " where tripID='" + tripid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            sum = c.getString(0);
        else
            sum = String.valueOf(-1);
        System.out.println("stoping fetch");
        return sum;
    }

    // Max entry id in entryInfo
    public String getEntryIdinEntryinfo() {
        System.out.println("starting fetch");
        String sum;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT MAX(entryId)  FROM " + DBHandler.ENTRY_INFO;
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            sum = c.getString(0);
        else
            sum = String.valueOf(-1);
        System.out.println("stoping fetch");
        return sum;
    }

    public String getmaxentryin_date() {
        System.out.println("starting fetch");
        String sum;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "SELECT MAX(date)  FROM " + DBHandler.ENTRY_INFO;
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            sum = c.getString(0);
        else
            sum = String.valueOf(-1);
        System.out.println("stoping fetch");
        return sum;
    }

    //getting trip amount
    public Double getTripUserEntryAmount(String tripId, String companyid, Boolean transType) {
        System.out.println("starting fetch");
        Double sum;
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        if (transType)
            s = "SELECT sum (amount)  FROM " + DBHandler.TRIP_ENTRY_INFO + " where tripID=" + tripId + " AND company_id='" + companyid + "'AND transactionType='" + 1 + "'";
        else
            s = "SELECT sum (amount)  FROM " + DBHandler.TRIP_ENTRY_INFO + " where tripID=" + tripId + " AND company_id='" + companyid + "'AND transactionType='" + 0 + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c.moveToFirst())
            sum = c.getDouble(0);
        else
            sum = Double.valueOf(Long.valueOf(-1));
        System.out.println("stoping fetch");
        return sum;
    }

    //getting all trip_info
    public ArrayList<TripInfo> getallTripInfo(String tripId, Boolean getall) {
        System.out.println("starting fetch");
        ArrayList<TripInfo> trip_names = new ArrayList<TripInfo>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        if ((getall) && tripId == null) {
            s = "SELECT *  FROM " + DBHandler.TRIP_INFO;
        } else {
            s = "SELECT *  FROM " + DBHandler.TRIP_INFO + " where trip_id='" + tripId + "'";
        }
        Cursor c = sdb.rawQuery(s, null);
        Log.e("cursur cout=", "" + c.getCount());
        if (c.moveToFirst()) {
            TripInfo dto;
            do {
                dto = new TripInfo();
                dto.setTripName(c.getString(c.getColumnIndex(DBHandler.TRIP_NAME)));
                dto.setTripId(c.getString(c.getColumnIndex(DBHandler.TRIP_WEB_ID)));
                dto.setOwner(c.getString(c.getColumnIndex(DBHandler.TRIP_OWNER)));
                trip_names.add(dto);
            } while (c.moveToNext());
        }
        c.close();
        System.out.println("stoping fetch");
        return trip_names;
    }

    //getting null entry_ids
    public ArrayList<EntryInfo> getallnullEntryId(Boolean isnull, int eyid) {
        System.out.println("starting fetch");
        ArrayList<EntryInfo> entries = new ArrayList<EntryInfo>();
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s = null;
        if (isnull)
            s = "SELECT *  FROM " + DBHandler.ENTRY_INFO + " where entryId is null  OR entryId=''";
        else if (!isnull)
            s = "SELECT *  FROM " + DBHandler.ENTRY_INFO + " where _id='" + eyid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                EntryInfo dto;
                do {
                    dto = new EntryInfo();
                    dto.setTransactionType(c.getString(c.getColumnIndex(DBHandler.EY_TRANSACTIONTYPE)));
                    dto.setTripId(c.getString(c.getColumnIndex(DBHandler.EY_TRIPID)));
                    dto.setEntryId(c.getString(c.getColumnIndex(DBHandler.EY_ENTRYID)));
                    dto.setDate(c.getString(c.getColumnIndex(DBHandler.EY_DATE)));
                    dto.setId(String.valueOf(c.getInt(c.getColumnIndex(DBHandler.EY_ID))));
                    dto.setGroupId(c.getString(c.getColumnIndex(DBHandler.EY_GROUP_ID)));
                    dto.setCompanyId(c.getString(c.getColumnIndex(DBHandler.EY_COMPANY_ID)));
                    dto.setCreditAccount(c.getString(c.getColumnIndex(DBHandler.EY_CREADITACCOUNT)));
                    dto.setDebitAccount(c.getString(c.getColumnIndex(DBHandler.EY_DEBITACCOUNT)));
                    dto.setAmount(c.getDouble(c.getColumnIndex(DBHandler.EY_AMOUNT)));
                    dto.setDescription(c.getString(c.getColumnIndex(DBHandler.EY_DESCRIPTION)));
                    entries.add(dto);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return entries;
    }

    //deleting trip_info
    public void deleteTrip(String tripId) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "DELETE FROM " + DBHandler.TRIP_INFO + " where trip_id='" + tripId + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
    }

    //deleting entry
    public void deleteEntry(int idval, Boolean trip, String entryid) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        String s;
        if (!trip)
            s = "DELETE FROM " + DBHandler.ENTRY_INFO + " where _id='" + idval + "'";
        else
            s = "DELETE FROM " + DBHandler.TRIP_ENTRY_INFO + " where tripID='"
                    + idval + "'AND trip_email='" + Prefs.getEmailId(context) + "'AND entryId='" + entryid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("deleted entry" + idval);
    }

    //deleting entry sync
    public void deleteEntry(Boolean trip, String entryid) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        String s;
        if (!trip)
            s = "DELETE FROM " + DBHandler.ENTRY_INFO + " where entryId='" + entryid + "'";
        else
            s = "DELETE FROM " + DBHandler.TRIP_ENTRY_INFO + " where entryId= '" + entryid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("deleted entry" + entryid);
    }

    //deleting email from table
    public void deleteEmail(String tripId, String email, String cid) {
        System.out.println("starting fetch");
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        s = "DELETE FROM " + DBHandler.TRIP_SHARE + " where trip_id='" + tripId + "'AND _email='" + email + "'AND companyId=" + cid + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
    }

    //add tripinfo List
    public void addtripshareInfo(ArrayList<TripShare> trips) {
        Log.d("Insertion in database", "Insertion database ");
        for (int j = 0; j < trips.size(); j++) {
            TripShare tripShare = trips.get(j);
            extraFunction();
            sdb = dbH.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHandler.TS_OWNER, tripShare.getOwner());
            values.put(DBHandler.TS_TRIP_ID, tripShare.getTripId());
            values.put(DBHandler.TS_EMAIL, tripShare.getEmail());
            values.put(DBHandler.TS_COMPANYNAME, tripShare.getCompanyName());
            values.put(DBHandler.TS_COMPANYID, tripShare.getCompanyId());
            values.put(DBHandler.TS_COMPANYNTYPE, tripShare.getCompanyType());
            long i = sdb.insertWithOnConflict(DBHandler.TRIP_SHARE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("Insertion in database", "Insertion database " + i + "" + tripShare.getEmail());
        }
    }

    //add tripInfo dto
    public void addtripshareInfodto(TripShare tripShare) {
        Log.d("Insertion in database", "Insertion database");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.TS_OWNER, tripShare.getOwner());
        values.put(DBHandler.TS_TRIP_ID, tripShare.getTripId());
        values.put(DBHandler.TS_EMAIL, tripShare.getEmail());
        values.put(DBHandler.TS_COMPANYNAME, tripShare.getCompanyName());
        values.put(DBHandler.TS_COMPANYID, tripShare.getCompanyId());
        values.put(DBHandler.TS_COMPANYNTYPE, tripShare.getCompanyType());

        long i = sdb.insertWithOnConflict(DBHandler.TRIP_SHARE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("Insertion in database", "Insertion database " + i + " " + tripShare.getEmail());
    }

    public long addaccountsdata(Accounts acdto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        long i = 0;
        if (checkaccountExist(acdto).size() == 0) {
            String firstchar = String.valueOf(acdto.getAccountName().charAt(0)).toUpperCase();
            String accountName = firstchar + acdto.getAccountName().substring(1);
            values.put(DBHandler.ACCOUNT_NAME, accountName);
            values.put(DBHandler.ACCOUNT_GROUP_ID, acdto.getGroupId());
            values.put(DBHandler.ACCOUNT_OPENING_BAL, acdto.getOpeningBalance());
            values.put(DBHandler.ACCOUNT_WEB_ID, acdto.getAcc_webId());
            values.put(DBHandler.ACCOUNT_UNIQUENAME, acdto.getUniqueName());
            values.put(DBHandler.ACCOUNT_SENDERID, acdto.getSenderId());
            values.put(DBHandler.ACCOUNT_NUMBER, acdto.getBank_account_number());
            values.put(DBHandler.ACCOUNT_IFSC_CODE, acdto.getBank_ifsc());
            i = sdb.insertWithOnConflict(DBHandler.ACCOUNTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            Log.e("name=" + acdto.getAccountName(), " webid=" + acdto.getAcc_webId() + " grpId=" + acdto.getGroupId());
            Log.d("Insertion in database", "Insertion database " + i + "" + acdto.getAccountName());
        } else {
            updateaccount(acdto);
        }
        return i;
    }

    public void addCompanyList(ArrayList<Company> companies) {
        Log.d("Insertion in database", "Insertion database ");
        ArrayList<Company> companies1 = new ArrayList<>();
        companies1 = companies;
        for (int i = 0; i < companies1.size(); i++) {
            Company dto = new Company();
            dto = companies1.get(i);
            if (checkcompanyExist(dto).size() == 0) {
                extraFunction();
                sdb = dbH.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DBHandler.COMPANY_NAME_, dto.getCompanyName());
                values.put(DBHandler.COMPANY_ID, dto.getCompanyId());
                values.put(DBHandler.COMPANY_EMAILID, dto.getEmailId());
                values.put(DBHandler.COMPANY_TYPE, dto.getCompanyType());
                values.put(DBHandler.FINANCIALYEAR, dto.getFinancialYear());
                long i1 = sdb.insert(DBHandler.COMPANYLIST, null, values);
                Log.d("Insertion in database", "Insertion database " + i1 + "" + dto.getCompanyName());
            } else {
                updatecompany(dto);
            }
        }
    }

    //Check tripexist
    public ArrayList<Company> checkcompanyExist(Company dto) {
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        ArrayList<Company> exist = new ArrayList<>();
        s = "SELECT *  FROM " + DBHandler.COMPANYLIST + " where company_id=" + dto.getCompanyId() +
                " AND emailId='" + dto.getEmailId() + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                Company tdto;
                do {
                    tdto = new Company();
                    tdto.setCompanyId(c.getString(c.getColumnIndex(DBHandler.COMPANY_ID)));
                    tdto.setCompanyName(c.getString(c.getColumnIndex(DBHandler.COMPANY_NAME_)));
                    tdto.setEmailId(c.getString(c.getColumnIndex(DBHandler.COMPANY_EMAILID)));
                    tdto.setCompanyType(c.getString(c.getColumnIndex(DBHandler.COMPANY_TYPE)));
                    exist.add(tdto);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return exist;
    }

    //Check tripexist
    public ArrayList<Accounts> checkaccountExist(Accounts dto) {
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        ArrayList<Accounts> exist = new ArrayList<>();
        s = "SELECT *  FROM " + DBHandler.ACCOUNTS + " where name LIKE '%" + dto.getAccountName().replaceAll("'", "") + "%'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                Accounts tdto;
                do {
                    tdto = new Accounts();
                    tdto.setAccountName(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_NAME)));
                    tdto.setAcc_webId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_WEB_ID)));
                    tdto.setOpeningBalance(c.getDouble(c.getColumnIndex(DBHandler.ACCOUNT_OPENING_BAL)));
                    tdto.setGroupId(c.getString(c.getColumnIndex(DBHandler.ACCOUNT_GROUP_ID)));
                    exist.add(tdto);
                    if (dto.getUniqueName() != null)
                        if ((dto.getAccountName().equalsIgnoreCase("Rent") && dto.getUniqueName().
                                equalsIgnoreCase("ExpenseRent") || dto.getUniqueName().equalsIgnoreCase("IncomeRent")) || (dto.getAccountName().equalsIgnoreCase("Other") && dto.getUniqueName().
                                equalsIgnoreCase("ExpenseOther") || dto.getUniqueName().equalsIgnoreCase("IncomeOther"))) {
                            exist.clear();
                        }
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return exist;
    }

    public void updatecompany(Company dto) {
        Log.d("updating in database", "updating database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.COMPANY_NAME_, dto.getCompanyName());
        values.put(DBHandler.COMPANY_TYPE, dto.getCompanyType());
        values.put(DBHandler.COMPANY_EMAILID, dto.getEmailId());
        values.put(DBHandler.COMPANY_ID, dto.getCompanyId());
        values.put(DBHandler.FINANCIALYEAR, dto.getFinancialYear());
        long i = sdb.update(DBHandler.COMPANYLIST, values, " company_id= '" + dto.getCompanyId() + "' AND emailId='" + dto.getEmailId() + "'", null);
        Log.d("updating in database", "updating database " + i + "" + dto.getCompanyName());
    }

    public long addgroupdata(GroupInfo gdto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.GRP_NAME, gdto.getGroupName());
        values.put(DBHandler.GRP_PARENT_ID, gdto.getParentId());
        values.put(DBHandler.GRP_SYSTEM_ID, gdto.getSystemId());
        long i = sdb.insert(DBHandler.GROUP_INFO, null, values);
        Log.d("Insertion in database", "Insertion database " + i + "" + gdto.getGroupName());
        return i;
    }

    public long adddeletedEntryid(String entryid) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.DELETED_ENTRYID, entryid);
        long i = sdb.insert(DBHandler.DELETED_ENTRYID, null, values);
        Log.d("Insertion in database", "Insertion database " + i + "" + entryid);
        return i;
    }

    public long addTripInfo(TripInfo dto) {
        Log.d("Insertion in database", "Insertion database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.TRIP_OWNER, dto.getOwner());
        values.put(DBHandler.TRIP_NAME, dto.getTripName());
        values.put(DBHandler.TRIP_WEB_ID, dto.getTripId());
        long i = sdb.insert(DBHandler.TRIP_INFO, null, values);
        Log.d("Insertion in database", "Insertion database " + i + "" + dto.getTripName());
        return i;
    }

    public long updateTripInfo(TripInfo dto, String trpid) {
        Log.d("updating in database", "updating database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.TRIP_OWNER, dto.getOwner());
        values.put(DBHandler.TRIP_NAME, dto.getTripName());
        values.put(DBHandler.TRIP_WEB_ID, dto.getTripId());
        long i = sdb.update(DBHandler.TRIP_INFO, values, "trip_id=" + trpid, null);
        Log.d("updating in database", "updating database " + i + "" + dto.getTripName());
        return i;
    }

    public long updateTripInfoshare(TripShare dto, String tripId, String cid, String mail) {
        Log.d("updating in database", "updating database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.TS_OWNER, dto.getOwner());
        values.put(DBHandler.TS_EMAIL, dto.getEmail());
        values.put(DBHandler.TS_TRIP_ID, dto.getTripId());
        values.put(DBHandler.TS_COMPANYID, dto.getCompanyId());
        values.put(DBHandler.TS_COMPANYNAME, dto.getCompanyName());
        values.put(DBHandler.TS_COMPANYNTYPE, dto.getCompanyType());

        long i = sdb.update(DBHandler.TRIP_SHARE, values, " trip_id= '" + tripId + "' AND companyId='" + cid + "' AND _email= '" + mail + "'", null);
        Log.d("updating in database", "updating database " + i + "" + dto.getEmail());
        return i;
    }

    //fill entry in db
    public long fillnullentry(String id, String entryid) {
        Log.d("update in database", "update database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.EY_ENTRYID, entryid);
        long i = sdb.update(DBHandler.ENTRY_INFO, values, " _id= '" + id + "'", null);
        return i;
    }

    public void updateTripInfosharelist(ArrayList<TripShare> trips) {
        Log.d("updating in database", "updating database ");
        for (int j = 0; j < trips.size(); j++) {
            TripShare dto = new TripShare();
            dto = trips.get(j);
            if (checktrip_exist(trips.get(j)).size() == 0) {
                ArrayList<TripShare> tripadd = new ArrayList<>();
                tripadd.add(trips.get(j));
                addtripshareInfo(tripadd);
                continue;
            }
            extraFunction();
            sdb = dbH.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHandler.TS_OWNER, dto.getOwner());
            values.put(DBHandler.TS_EMAIL, dto.getEmail());
            values.put(DBHandler.TS_TRIP_ID, dto.getTripId());
            values.put(DBHandler.TS_COMPANYNTYPE, dto.getCompanyType());
            values.put(DBHandler.TS_COMPANYNAME, dto.getCompanyName());
            values.put(DBHandler.TS_COMPANYID, dto.getCompanyId());
            long i = sdb.update(DBHandler.TRIP_SHARE, values, " trip_id= '" + trips.get(j).getTripId() + "' AND companyId='" + trips.get(j).getCompanyId() + "'AND _email='" + trips.get(j).getEmail() + "'", null);
            Log.d("updating in database", "updating database " + i + "" + dto.getEmail());
        }
    }

    //Check tripexist
    public ArrayList<TripShare> checktrip_exist(TripShare dto) {
        extraFunction();
        sdb = dbH.getReadableDatabase();
        String s;
        ArrayList<TripShare> exist = new ArrayList<>();
        s = "SELECT *  FROM " + DBHandler.TRIP_SHARE + " where trip_id=" + dto.getTripId() +
                " AND _email='" + dto.getEmail() + "'";
        Cursor c = sdb.rawQuery(s, null);
        if (c != null) {
            if (c.moveToFirst()) {
                TripShare tdto;
                do {
                    tdto = new TripShare();
                    tdto.setEmail(c.getString(c.getColumnIndex(DBHandler.TS_EMAIL)));
                    tdto.setTripId(c.getString(c.getColumnIndex(DBHandler.TS_TRIP_ID)));
                    tdto.setOwner(c.getString(c.getColumnIndex(DBHandler.TS_OWNER)));
                    exist.add(tdto);
                } while (c.moveToNext());
            }
            c.close();
        }
        System.out.println("stoping fetch");
        return exist;
    }

    public long updateaccount(Accounts acdto) {
        Log.d("update in database", "update database ");
        extraFunction();
        sdb = dbH.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.ACCOUNT_NAME, acdto.getAccountName());
        // values.put(DBHandler.ACCOUNT_GROUP_ID, acdto.getGroupId());
        //values.put(DBHandler.ACCOUNT_OPENING_BAL, acdto.getOpeningBalance());
        // values.put(DBHandler.ACCOUNT_WEB_ID, acdto.getAcc_webId());
        values.put(DBHandler.ACCOUNT_UNIQUENAME, acdto.getUniqueName());
        long i = sdb.update(DBHandler.ACCOUNTS, values, " account_id= '" + acdto.getAcc_webId() + "'", null);
        Log.d("update in database", "update database " + i + "" + acdto.getAccountName());
        return i;
    }

    public void deletedatabase(Context ct) {
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.ACCOUNTS);
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.GROUP_INFO);
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.ENTRY_INFO);
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.TRIP_INFO);
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.TRIP_ENTRY_INFO);
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.TRIP_SHARE);
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.COMPANYLIST);
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.DELETED_ENTRYID);
        sdb.execSQL("DROP TABLE IF EXISTS " + DBHandler.SMS_DATA);
        sdb = dbH.getWritableDatabase();
        dbH.onClear(sdb);
    }

    public int deleteAllgrpinfo() {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.GROUP_INFO;
        sdb = dbH.getWritableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int delete_account(String webid) {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.ACCOUNTS + " where account_id='" + webid + "'";
        sdb = dbH.getWritableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int deleteAlltrips() {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.TRIP_INFO;
        sdb = dbH.getWritableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int deleteAlltripentry() {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.TRIP_ENTRY_INFO;
        sdb = dbH.getWritableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int deleteAlltripshare() {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.TRIP_SHARE;
        sdb = dbH.getWritableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int deleteAllcompanylist() {
        extraFunction();
        String countQuery = "DELETE   FROM " + DBHandler.COMPANYLIST;
        sdb = dbH.getWritableDatabase();
        Cursor cursor = sdb.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
}



