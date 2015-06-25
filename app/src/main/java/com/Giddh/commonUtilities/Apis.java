package com.Giddh.commonUtilities;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class Apis {
    public static Apis apis;
    public static String ErrorResponse = "0";
    public static String SuccessResponse = "1";
    public static int SuccessResponseint = 1;
    Context ct;
    String serverName;
    String completeUrl;
    String response = "";
    Boolean logoutavail = false;
    String UserIdType = "1";

    private Apis(Context c) {
        ct = c;
    }

    public static Apis getApisInstance(Context ctx) {
        apis = new Apis(ctx);
        return apis;
    }

    //API FOR SIGNUP WITH GOOGLE
    public String signupWithGoogleFacebook(String accessToken, String accessType) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "generateAuthKey";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ACCESSTOKEN, accessToken));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ACCESSTYPE, accessType));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("signupGoogleFacebook", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("signup with google fb", "" + response);
        return response;
    }


    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    //API FOR ADDING ACCOUNT
    public String addAccount(String groupName, String accountName, String uniqueName, String openingBalance) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "addAccountApi";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME, Prefs.getCompanyName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.GROUP_NAME, groupName));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.OPENING_BAL, openingBalance));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.UNIQUENAME, ""));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ACCOUNT_NAME, accountName));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("adding Account", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("adding Account", "" + response);
        return response;
    }

    //API FOR DELETE ENTRY
    public String deleteEntry(String entry_id) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "deleteEntry";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME, Prefs.getCompanyName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ENTRY_ID, entry_id));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DELETE_STATUS, "1"));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("deleting entry", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("deleting entry", "" + response);
        return response;
    }

    //API FOR DELETE ACCOUNT
    public String deleteAccount(String accName, String groupname) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "deleteAccount";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME, Prefs.getCompanyName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ACCOUNT_NAME, accName));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.UNIQUENAME, ""));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.GROUP_NAME, groupname));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("deleting account", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("deleting account", "" + response);
        return response;
    }

    //API FOR EDIT IN TRIP
    public String editTrip(String tripName, String tripid) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = "http://giddh.com:8080/giddh/tripedit.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_NAME, tripName));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripid));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("editInTrip", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("editInTrip", "" + response);
        return response;
    }

    //API FOR TRIALBALANCE
    public String getTrialBalance(String Company, String fromDate, String toDate) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "trialBalanceNew";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME, Company));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.FROM_DATE, fromDate));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TO_DATE, toDate));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("signupGoogleFacebook", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("signup with google fb", "" + response);
        return response;
    }

    //API FOR TRIP SUMMARY
    public String getTripSummary(String tripId, String EntryId) {
        completeUrl = "http://www.giddh.com:8080/giddh/tripsummary.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        if (EntryId == null)
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ENTRY_ID, ""));
        else
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ENTRY_ID, EntryId));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("getting__trip_summary", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("getting_trip_summary", "" + response);
        return response;
    }

    //API FOR ENTRY SUMMARY
    public String getEntrySummary(String EntryId) {
        completeUrl = "http://www.giddh.com:8080/giddh/entrysummary.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        if (EntryId == null)
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ENTRY_ID, ""));
        else
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ENTRY_ID, EntryId));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("gettingEntry_summary", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("getting_entry_summary", "" + response);
        return response;
    }

    //API FOR EXIT BTRIP
    public String exitTrip(String tripId) {
        completeUrl = "http://giddh.com:8080/giddh/tripdelete.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("deleting Trip", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("deleting Trip", "" + response);
        return response;
    }


    //API FOR GETALLDEL
    public String getalldelete() {
        completeUrl = "http://giddh.com:8080/account/entry/getAllDeleted";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("alldel ", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("alldel ", "" + response);
        return response;
    }

    //API FOR REMOVE EMAIL
    public String RemoveEmail(String tripId, String email) {
        completeUrl = "http://giddh.com:8080/giddh/tripemailremove.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.EMAIL, email));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("deleting email", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("deleting email", "" + response);
        return response;
    }

    //API FOR ACCOUNT LIST
    public String getAccountList() {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "accountList";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME, Prefs.getCompanyName(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("accountList", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("accountList", "" + response);
        return response;
    }

    //API FOR EDIT ENTRY
    public String editentry(String JsonDebit, String JsonCredit, String tripId, String description, String date, String entryid) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "updateEntry";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DEBIT, JsonDebit));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CREDIT, JsonCredit));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.ENTRY_ID, entryid));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        //  nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.SHOWAS, "0"));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME, Prefs.getCompanyName(ct)));
        if (tripId != null)
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripId));
        else
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, ""));
        if (description == null)
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DESCRIPTION, ""));
        else
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DESCRIPTION, description));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATE, date));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("Entryupdated", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("Entryupdated", "" + response);
        return response;
    }

    //API FOR SEARCH
    public String getSearchResult(String Company, String Searchtext, String todate, String fromdate) {
        //serverName = Prefs.getServerUrl(ct);
        completeUrl = "http://giddh.com:8080/giddh/trialbalance.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.FROM_DATE, fromdate));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TO_DATE, todate));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CNAME, Company));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.SEARCHBY, Searchtext));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("SearchResult", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("SearchResult", "" + response);
        return response;
    }

    //API FOR CREATING TRIP
    public String createTrip(String TripName) {
        //serverName = Prefs.getServerUrl(ct);
        completeUrl = "http://giddh.com:8080/giddh/tripcreate.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_NAME, TripName));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            Log.e("tripurl", "" + nameValuePair);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("creatTrip", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("creatTrip", "" + response);
        return response;
    }

    //API FOR SHARING TRIP
    public String shareTrip(String email, String tripId) {
        //serverName = Prefs.getServerUrl(ct);
        completeUrl = "http://giddh.com:8080/giddh/tripshare.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.EMAIL, email));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("shareTrip", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("shareTrip", "" + response);
        return response;
    }

    //API FOR GETTING TRIPINFO List
    public String getTripInfo(String tripId) {
        completeUrl = "http://giddh.com:8080/giddh/tripgetinfo.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("TripInfo", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("TripInfo", "" + response);
        return response;
    }

    //API FOR GETTING TRIPINFO mail
    public String getTripInfomails(String tripId) {
        completeUrl = "http://giddh.com:8080/giddh/tripgetinfo.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("TripInfo", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("TripInfo", "" + response);
        return response;
    }

    //API FOR COMPANY LIST
    public String getCompanyNames() {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "companyListApi";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("company list", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("company list", "" + response);
        return response;
    }

    //Update Profile Name
    public String changeProfileName(String newname) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "updateCompanyName";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME, Prefs.getCompanyName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME_NEW, newname));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("company list", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("company list", "" + response);
        return response;
    }

    //API CREATING ENTRY
    public String doEntry(String JsonDebit, String JsonCredit, String tripId, String description, String date, String transactiontype) {
        serverName = Prefs.getServerUrl(ct);
        completeUrl = serverName + "createEntry";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DEBIT, JsonDebit));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.CREDIT, JsonCredit));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.MOBILE, "1"));
        //  nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.SHOWAS, "0"));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_NAME, Prefs.getCompanyName(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRIP_ID, tripId));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.TRANSACTIONTYPE, transactiontype));
        if (description == null)
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DESCRIPTION, ""));
        else
            nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DESCRIPTION, description));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.DATE, date));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("Entry in db", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("Entry in db", "" + response);
        return response;
    }

    //API FOR TRIPLIST
    public String getTripList() {
        //serverName = Prefs.getServerUrl(ct);
        completeUrl = "http://giddh.com:8080/giddh/tripgetlist.jsp";
        HttpPost httpPost = new HttpPost(completeUrl);
        List<NameValuePair> nameValuePair = new ArrayList<>();
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.AUTH_ID, Prefs.getAuthkey(ct)));
        nameValuePair.add(new BasicNameValuePair(VariableClass.ResponseVariables.COMPANY_ID, Prefs.getCompanyId(ct)));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return response;
        }
        logoutavail = false;
        Log.e("trip list", "" + nameValuePair);
        response = HitUrl(httpPost);
        Log.e("trip list", "" + response);
        return response;
    }

    public String HitUrl(HttpPost postrequest) {
        Log.e("hitting url", "hitting url ");
        if (CommonUtility.isNetworkAvailable(ct)) {
            long backoff;
            final int MAX_ATTEMPTS = 1;
            final int BACKOFF_MILLI_SECONDS = 2000;
            final Random random = new Random();
            StringBuilder total = null;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 6 * 1000);
            HttpConnectionParams.setSoTimeout(httpParameters, 6 * 1000);
            DefaultHttpClient httpClient;
            InputStream inputstream;
            backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                try {
                    httpClient = new DefaultHttpClient();
                    httpClient.setParams(httpParameters);
                    HttpResponse httpresponse = httpClient.execute(postrequest);
                    String line;
                    inputstream = httpresponse.getEntity().getContent();
                    total = new StringBuilder();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(inputstream));
                    while ((line = rd.readLine()) != null) {
                        total.append(line);
                    }
                    break;
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    // CommonUtility.showCustomAlertForContacts(ct.getApplicationContext(), ct.getString(R.string.server_error));
                    return "";
                } catch (Exception e) {
                    e.printStackTrace();
                    if (i == MAX_ATTEMPTS) {
                        return "";
                    }
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException e1) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                    backoff *= 2;
                }
            }
            if (total != null)//if response is null
            {
                if (total.toString().contains("1014")) {
                    Log.e("logout", "logout" + logoutavail);

                   /* if (logoutavail)
                        CommonUtility.logOut(ct);*/
                }
                return total.toString();
            } else return "";
        }
        return "";
    }
}
