package com.Giddh.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.Giddh.R;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.AccountDetails;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.CompanyDetails;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.GroupDetails;
import com.Giddh.dtos.userTransactions;
import com.Giddh.ui.Activities.SplashScreen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncomingSms extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    userTransactions seleceteddto;
    EntryInfo entryInfo;
    UserService userService;
    String senderId, message;

    public void onReceive(Context context, Intent intent) {
   /*     // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        userService = UserService.getUserServiceInstance(context);
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    senderId = currentMessage.getDisplayOriginatingAddress().toLowerCase();
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd");
                    String currentDate = sdfd.format(new Date());
                    message = currentMessage.getDisplayMessageBody().toLowerCase();
                    Log.e("message_aaya", message + " " + senderId);
                    entryInfo = new EntryInfo();
                    entryInfo.setDate(currentDate);
                    new ParseSms(context).execute();
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }*/
    }

    private void showNotification(Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SplashScreen.class), 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Transaction found")
                        .setContentText(message);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    class ParseSms extends AsyncTask<Void, Void, Void> {
        Context mContext;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        public ParseSms(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (senderId.contains("-")) {
                String[] parts = senderId.split("-");
                senderId = (parts[1].toLowerCase());
                Accounts accounts = new Accounts();
                accounts.setAccountName(senderId);
                int id = userService.getmaxaccId() + 1;
                accounts.setAcc_webId(String.valueOf(id));
                accounts.setGroupId("3");
                accounts.setOpeningBalance(0);
                userService.addaccountsdata(accounts);
                Pattern regEx
                        = Pattern.compile("(?:inr|rs)+[\\s]*[0-9]+[\\.]*[0-9]+");
                // Find instance of pattern matches
                Matcher m = regEx.matcher(message);
                if (m.find()) {
                    try {
                        Log.e("amount_value= ", "" + m.group(0));
                        String amount = (m.group(0).replaceAll("inr", ""));
                        amount = amount.replaceAll("rs", "");
                        amount = amount.replaceAll("inr", "");
                        amount = amount.replaceAll(" ", "");
                        entryInfo.setAmount(Double.valueOf(amount));
                        if (message.contains("debited") ||
                                message.contains("purchasing") || message.contains("purchase") || message.contains("dr")) {
                            entryInfo.setTransactionType("0");
                        } else if (message.contains("credited") || message.contains("cr")) {
                            entryInfo.setTransactionType("1");
                        }
                        Log.e("matchedValue= ", "" + amount);
                        if (!Character.isDigit(senderId.charAt(0)))
                            if (entryInfo.getTransactionType().equals("0")) {
                                entryInfo.setDebitAccount(userService.getinfoExpacc("Cash", "3").getAcc_webId());
                                entryInfo.setCreditAccount(userService.getinfoExpacc(senderId, "3").getAcc_webId());
                            } else {
                                entryInfo.setCreditAccount(userService.getinfoExpacc("Other", "1").getAcc_webId());
                                entryInfo.setDebitAccount(userService.getinfoExpacc(senderId, "3").getAcc_webId());
                            }
                        if (entryInfo.getTransactionType() != null) {
                            userService.addentrydata(entryInfo);
                            showNotification(mContext);
                            CommonUtility.syncwithServer(mContext);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}

