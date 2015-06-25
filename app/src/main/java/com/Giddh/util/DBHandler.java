package com.Giddh.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.Giddh.commonUtilities.Prefs;

/**
 * Created by walkover on 7/3/15.
 */
public class DBHandler extends SQLiteOpenHelper {
    //DB_NAME
    public static final String DATABASE_NAME = "GidhhDB";
    //table names
    public static final String GROUP_INFO = "group_info";
    public static final String ENTRY_INFO = "entry_info";
    public static final String TRIP_INFO = "trip_info";
    public static final String TRIP_ENTRY_INFO = "trip_entry_info";
    public static final String TRIP_SHARE = "trip_share";
    public static final String ACCOUNTS = "accounts";
    public static final String COMPANYLIST = "company_list";
    public static final String SMS_DATA = "_sms";
    public static final String DELETED_ENTRYID = "deleted_entry";
    //column names for storing accounts
    public static final String ACCOUNT_ID = "_id";
    public static final String ACCOUNT_NAME = "name";
    public static final String ACCOUNT_GROUP_ID = "group_id";
    public static final String ACCOUNT_OPENING_BAL = "opening_bal";
    public static final String ACCOUNT_WEB_ID = "account_id";
    public static final String ACCOUNT_UNIQUENAME = "uniqueName";
    public static final String ACCOUNT_SENDERID = "sender_id";
    String CREATE_ACCOUNTS = " CREATE TABLE" + " " + ACCOUNTS + "("
            + ACCOUNT_SENDERID + " TEXT," + ACCOUNT_NAME + " TEXT," + ACCOUNT_UNIQUENAME + " TEXT," + ACCOUNT_WEB_ID + " INT," + ACCOUNT_OPENING_BAL + " TEXT," + ACCOUNT_GROUP_ID + " TEXT," + ACCOUNT_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT " + ");";
    //column names for storing accounts
    public static final String SMSID = "sms_id";
    public static final String SMS_PRIMARY_ID = "_id";
    public static final String PARSESTATUS = "name";
    String CREATE_SMS = " CREATE TABLE" + " " + SMS_DATA + "("
            + PARSESTATUS + " TEXT," + SMSID + " TEXT," + ACCOUNT_NAME + SMS_PRIMARY_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT " + ");";
    public static final String DELID = "_id";
    public static final String DELTEDENTRY = "entry_id";
    String CREATE_DEL_ENTRYID = " CREATE TABLE" + " " + DELETED_ENTRYID + "("
            + DELTEDENTRY + " TEXT," + DELID + " INTEGER  PRIMARY KEY AUTOINCREMENT " + ");";
    //column names for storing companylist
    public static final String COMPANY_ = "_id";
    public static final String COMPANY_NAME_ = "companyName";
    public static final String COMPANY_TYPE = "companyType";
    public static final String FINANCIALYEAR = "financial_yeR";
    public static final String COMPANY_EMAILID = "emailId";
    public static final String COMPANY_ID = "company_id";
    String CREATE_COMPANYLIST = " CREATE TABLE" + " " + COMPANYLIST + "("
            + COMPANY_ID + " TEXT," + FINANCIALYEAR + " TEXT," + COMPANY_NAME_ + " TEXT," + COMPANY_TYPE + " TEXT," + COMPANY_EMAILID + " TEXT," + COMPANY_ + " INTEGER  PRIMARY KEY AUTOINCREMENT " + ");";
    //column names for storing group info
    public static final String GRP_ID = "_id";
    public static final String GRP_NAME = "name";
    public static final String GRP_PARENT_ID = "parent_id";
    public static final String GRP_SYSTEM_ID = "system_id";
    String CREATE_GROUP_INFO = " CREATE TABLE" + " " + GROUP_INFO + "("
            + GRP_NAME + " TEXT," + GRP_SYSTEM_ID + " TEXT," + GRP_PARENT_ID + " TEXT," + GRP_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT " + ");";
    //column names for storing entry info
    public static final String EY_ID = "_id";
    public static final String EY_COMPANY_ID = "company_id";
    public static final String EY_DATE = "date";
    public static final String EY_AMOUNT = "amount";
    public static final String EY_GROUP_ID = "group_id";
    public static final String EY_DEBITACCOUNT = "debit_account";
    public static final String EY_DESCRIPTION = "description_id";
    public static final String EY_CREADITACCOUNT = "creadit_account";
    public static final String EY_TRANSACTIONTYPE = "transactionType";
    public static final String EY_ENTRYID = "entryId";
    public static final String EY_TRIPID = "tripID";
    public static final String EY_EMAILLOAN = "email_loan";
    String CREATE_ENTRY_INFO = " CREATE TABLE" + " " + ENTRY_INFO + "("
            + EY_COMPANY_ID + " TEXT," + EY_EMAILLOAN + " TEXT," + EY_ENTRYID + " TEXT," + EY_TRANSACTIONTYPE + " TEXT," + EY_TRIPID + " TEXT," + EY_DATE + " TEXT," + EY_DEBITACCOUNT + " TEXT," + EY_AMOUNT + " Double," + EY_GROUP_ID + " TEXT," + EY_CREADITACCOUNT + " TEXT," + EY_DESCRIPTION + " TEXT," + EY_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT " + ");";
    //column names for storing trip info
    public static final String TRIP_ID = "_id";
    public static final String TRIP_WEB_ID = "trip_id";
    public static final String TRIP_NAME = "tripName";
    public static final String TRIP_OWNER = "trip_owner";
    String CREATE_TRIP_INFO = " CREATE TABLE" + " " + TRIP_INFO + "("
            + TRIP_WEB_ID + " TEXT," + TRIP_NAME + " TEXT," + TRIP_OWNER + " TEXT," + TRIP_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT " + ");";
    //column names for storing trip entry
    public static final String TEY_ID = "_id";
    public static final String TEY_COMPANY_ID = "company_id";
    public static final String TEY_DATE = "date";
    public static final String TEY_AMOUNT = "amount";
    public static final String TEY_GROUP_ID = "group_id";
    public static final String TEY_DEBITACCOUNT = "debit_account";
    public static final String TEY_DESCRIPTION = "description_id";
    public static final String TEY_CREDITACCOUNT = "credit_account";
    public static final String TEY_TRANSACTIONTYPE = "transactionType";
    public static final String TEY_ENTRYID = "entryId";
    public static final String TEY_TRIPID = "tripID";
    public static final String TEY_EMAIL = "trip_email";
    String CREATE_TRIP_ENTRY = " CREATE TABLE" + " " + TRIP_ENTRY_INFO + "("
            + TEY_COMPANY_ID + " TEXT," + TEY_AMOUNT + " Double," + TEY_GROUP_ID + " TEXT," + TEY_DEBITACCOUNT + " TEXT," + TEY_DESCRIPTION + " TEXT," + TEY_CREDITACCOUNT + " TEXT," + TEY_TRANSACTIONTYPE + " TEXT," + TEY_ENTRYID + " TEXT," + TEY_TRIPID + " TEXT," + TEY_EMAIL + " TEXT," + TEY_DATE + " TEXT," + TEY_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT " + ");";
    //column names for storing trip share
    public static final String TS_ID = "_id";
    public static final String TS_TRIP_ID = "trip_id";
    public static final String TS_OWNER = "owner";
    public static final String TS_EMAIL = "_email";
    public static final String TS_COMPANYNAME = "companyName";
    public static final String TS_COMPANYNTYPE = "companyType";
    public static final String TS_COMPANYID = "companyId";

    String CREATE_TRIP_SHARE = " CREATE TABLE" + " " + TRIP_SHARE + "("
            + TS_COMPANYID + " TEXT,"+ TS_COMPANYNTYPE + " TEXT,"  + TS_TRIP_ID + " TEXT," + TS_COMPANYNAME + " TEXT," + TS_EMAIL + " TEXT," + TS_OWNER + " TEXT" + ",PRIMARY KEY(_email, companyId));";

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ENTRY_INFO);
        db.execSQL(CREATE_GROUP_INFO);
        db.execSQL(CREATE_TRIP_ENTRY);
        db.execSQL(CREATE_TRIP_SHARE);
        db.execSQL(CREATE_TRIP_INFO);
        db.execSQL(CREATE_ACCOUNTS);
        db.execSQL(CREATE_SMS);
        db.execSQL(CREATE_COMPANYLIST);
        db.execSQL(CREATE_DEL_ENTRYID);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(CREATE_ACCOUNTS);
            db.execSQL(CREATE_TRIP_INFO);
            db.execSQL(CREATE_TRIP_SHARE);
            db.execSQL(CREATE_ENTRY_INFO);
            db.execSQL(CREATE_TRIP_ENTRY);
            db.execSQL(CREATE_COMPANYLIST);
            db.execSQL(CREATE_DEL_ENTRYID);
            db.execSQL(CREATE_GROUP_INFO);
            db.execSQL(CREATE_SMS);
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public void onClear(SQLiteDatabase db) {
        onCreate(db);
    }
}
