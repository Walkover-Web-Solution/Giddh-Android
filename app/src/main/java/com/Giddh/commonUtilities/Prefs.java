package com.Giddh.commonUtilities;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {


    private static final String USER_NAME = "username";
    private static final String SERVER_URL = "server_url";
    private static final String AUTHKEY = "auth_key";
    private static final String COMPANY_ID = "company_id";
    private static final String EMAIL_ID = "email_id";
    private static final String COMPANY_NAME = "company_name";
    private static final String UPDATE_DATE = "update_date";
    private static final String CURRENCY = "currency";
    private static final String COUNTRY = "country";
    private static final String SIZEMAIL = "mails";
    private static final String FIRSTTIME = "first_time";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("mypref", Context.MODE_MULTI_PROCESS | Context.MODE_PRIVATE);
    }

    public static String getServerUrl(Context context) {
        return getPrefs(context).getString(SERVER_URL, "http://giddh.com/api/");
    }

    public static String getAuthkey(Context context) {
        return getPrefs(context).getString(AUTHKEY, "");
    }

    public static void setAuthkey(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(AUTHKEY, value).commit();
    }
    public static int getSizemail(Context context) {
        return getPrefs(context).getInt(SIZEMAIL, 0);
    }

    public static void setSizemail(Context context, int value) {
        // perform validation etc..
        getPrefs(context).edit().putInt(SIZEMAIL, value).commit();
    }


    public static String getEmailId(Context context) {
        return getPrefs(context).getString(EMAIL_ID, "");
    }

    public static void setEmailId(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(EMAIL_ID, value).commit();
    }


    public static String getFirsttime(Context context) {
        return getPrefs(context).getString(FIRSTTIME, "");
    }

    public static void setFirsttime(Context context, String value) {
        // perform validation etc..
        getPrefs(context).edit().putString(FIRSTTIME, value).commit();
    }
    public static String getUserName(Context context) {
        return getPrefs(context).getString(USER_NAME, "");
    }

    public static void setUserName(Context context, String value) {
        getPrefs(context).edit().putString(USER_NAME, value).commit();
    }

    public static String getCompanyId(Context context) {
        return getPrefs(context).getString(COMPANY_ID, "");
    }

    public static void setCompanyId(Context context, String value) {
        getPrefs(context).edit().putString(COMPANY_ID, value).commit();
    }

    public static String getCompanyName(Context context) {
        return getPrefs(context).getString(COMPANY_NAME, "");
    }

    public static void setCompanyName(Context context, String value) {
        getPrefs(context).edit().putString(COMPANY_NAME, value).commit();
    }

    public static String getUpdateDate(Context context) {
        return getPrefs(context).getString(UPDATE_DATE, "");
    }

    public static void setCurrency(Context context, String value) {
        getPrefs(context).edit().putString(CURRENCY, value).commit();
    }

    public static String getCurrency(Context context) {
        return getPrefs(context).getString(CURRENCY, "");
    }
    public static void setCountry(Context context, String value) {
        getPrefs(context).edit().putString(COUNTRY, value).commit();
    }

    public static String getCountry(Context context) {
        return getPrefs(context).getString(COUNTRY, "");
    }

    public static void setUpdateDate(Context context, String value) {
        getPrefs(context).edit().putString(UPDATE_DATE, value).commit();
    }

    public static void deletePrefs(Context context) {
        getPrefs(context).edit().clear().commit();
    }


}