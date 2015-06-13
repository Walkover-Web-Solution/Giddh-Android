package com.Giddh.commonUtilities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.Giddh.R;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.SmsDto;
import com.Giddh.dtos.SummaryAccount;
import com.Giddh.ui.Activities.SignUpHome;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.Session;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtility {
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public final static String BUGSENSEID = "6fd281fc";
    public final static String BUGSENSELIVE = "e16d9e0b";
    public final static String BUGSENSEID_TEST = "6fd281fc";
    public static MaterialDialog dialog;
    public static Boolean firstTIme;
   // public static int summary_stack=0;
    public static ArrayList<SummaryAccount> summary_stack =new ArrayList<>(3);

    public static boolean isNetworkAvailable(Context context) {
        boolean bool;
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        State mobile = conMan.getNetworkInfo(0).getState();
        State wifi = conMan.getNetworkInfo(1).getState();
        if (mobile == State.CONNECTED) {
            bool = true;
            return bool;
        } else if (wifi == State.CONNECTED) {
            bool = true;
            return bool;
        } else {
            bool = false;
            return bool;
        }
    }

    public static void show_PDialog(Context context
    ) {
        dialog = new MaterialDialog.Builder(context)
                .title("Loading")
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    public static void clearData(Context ctx, Activity act) {
        //delete database
        UserService us = UserService.getUserServiceInstance(ctx);
        callFacebookLogout(ctx);
        us.deletedatabase(ctx);
        /*us.deleteAllAcc();
        us.deleteAllcompanylist();
        us.deleteAllEntry();
        us.deleteAllgrpinfo();
        us.deleteAlltripentry();
        us.deleteAlltrips();
        us.deleteAlltripshare();*/
        Prefs.deletePrefs(ctx);
    }

    public static String getgroupIdName(String groupNI) {
        HashMap<String, String> groups = new HashMap();
        groups.put("0", "income");
        groups.put("0", "Income");
        groups.put("1", "Expense");
        groups.put("2", "Liability");
        groups.put("3", "Assets");
        groups.put("1", "expense");
        groups.put("2", "liability");
        groups.put("3", "assets");
        groups.put("income", "0");
        groups.put("Income", "0");
        groups.put("Expense", "1");
        groups.put("Liability", "2");
        groups.put("Assets", "3");
        groups.put("expense", "1");
        groups.put("liability", "2");
        groups.put("assets", "3");
        String name = groups.get(groupNI);
        return name;
    }

    public static String getJsonArray(String amount, String DebitCreditName, Context ctx, String id) {
        JSONArray jsonArr = null;
        JSONObject entryobjdebit = new JSONObject();
        UserService userService = UserService.getUserServiceInstance(ctx);
        try {
            entryobjdebit.put("accountName", userService.getaccountnameorId(DebitCreditName).getAccountName());
            entryobjdebit.put("uniqueName", userService.getaccountnameorId(DebitCreditName).getUniqueName());
            entryobjdebit.put("amount", amount);
            entryobjdebit.put("groupName", CommonUtility.getgroupIdName(userService.getaccountnameorId(DebitCreditName).getGroupId()));
            entryobjdebit.put("subGroup", CommonUtility.getgroupIdName(userService.getaccountnameorId(DebitCreditName).getGroupId()));
            jsonArr = new JSONArray();
            jsonArr.put(entryobjdebit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArr.toString();
    }

    public static void syncwithServer(Context ctx1) {
        UserService userService = UserService.getUserServiceInstance(ctx1);
        String maxid = userService.getEntryIdinEntryinfo();
        Prefs.setUpdateDate(ctx1, DateFormat.getDateTimeInstance().format(new Date()));
        new getaccountSummary(ctx1).execute(maxid);
    }

    static class getaccountSummary extends AsyncTask<String, Void, Void> {
        String response = null;
        Boolean iserr = false;
        UserService userService;
        private Context mContext;
        ArrayList<EntryInfo> nullEntries = new ArrayList<>();

        public getaccountSummary(Context context) {
            mContext = context;
            userService = UserService.getUserServiceInstance(mContext);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
           /* if (VariableClass.Vari.MESSEGE_FIRST_TIME) {
                new AlertDialogWrapper.Builder(mContext)
                        .setTitle("Would you like to get Entries from SMS?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (CommonUtility.dialog == null)
                            CommonUtility.show_PDialog(mContext);
                        CommonUtility.fetchInbox(mContext);
                        CommonUtility.syncwithServer(mContext);
                        VariableClass.Vari.MESSEGE_FIRST_TIME = false;
                    }
                }).show();
            }*/
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            response = Apis.getApisInstance(mContext).getEntrySummary(params[0]);
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                JSONArray jachild = null;
                JSONObject jobjin = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
//                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        //   response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        if (japarent.length() > 0)
                            for (int i = 0; i < japarent.length(); i++) {
                                EntryInfo entryInfo = new EntryInfo();
                                jochild = japarent.getJSONObject(i);
                                if (true) {
                                    // entryInfo.setTripId(params[0]);
                                    jachild = new JSONArray();
                                    jobjin = new JSONObject();
                                    jachild = jochild.getJSONArray(VariableClass.ResponseVariables.DEBIT);
                                    jobjin = jachild.getJSONObject(0);
                                    entryInfo.setAmount(Double.parseDouble(jobjin.getString(VariableClass.ResponseVariables.AMOUNT)));
                                    entryInfo.setTripId(jochild.getString(VariableClass.ResponseVariables.TRIP_ID));
                                    entryInfo.setDebitAccount(String.valueOf((userService.getaccountnameorId(jobjin.getString(VariableClass.ResponseVariables.ACCOUNT_NAME)).get_id())));
                                    String grpDebit = jobjin.getString(VariableClass.ResponseVariables.GROUP_NAME);
                                    entryInfo.setDate(jochild.getString(VariableClass.ResponseVariables.ENTRY_DATE));
                                    jachild = jochild.getJSONArray(VariableClass.ResponseVariables.CREDIT);
                                    jobjin = jachild.getJSONObject(0);
                                    entryInfo.setCreditAccount(String.valueOf(userService.getaccountnameorId(jobjin.getString(VariableClass.ResponseVariables.ACCOUNT_NAME)).get_id()));
                                    entryInfo.setEntryId(jochild.getString(VariableClass.ResponseVariables.ENTRY_ID));
                                    if (grpDebit.equalsIgnoreCase("Income") || jobjin.getString(VariableClass.ResponseVariables.GROUP_NAME).equalsIgnoreCase("Liability") || jobjin.getString(VariableClass.ResponseVariables.GROUP_NAME).equalsIgnoreCase("Income")) {
                                        entryInfo.setTransactionType("0");
                                    } else
                                        entryInfo.setTransactionType("1");
                                    userService.addentrydata(entryInfo);
                                }
                            }
                        nullEntries = userService.getallnullEntryId(true, 0);
                        for (int i = 0; i < nullEntries.size(); i++) {
                            new DoEntry(mContext).execute(nullEntries.get(i));
                        }
                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = mContext.getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = mContext.getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

    static class DoEntry extends AsyncTask<EntryInfo, Void, Void> {
        String response = null;
        Boolean iserr = false;
        UserService userService;
        private Context mContext;

        public DoEntry(Context context) {
            mContext = context;
            userService = UserService.getUserServiceInstance(mContext);
        }

        @Override
        protected void onPostExecute(Void result) {
            if (iserr) {
                //showErrorMessage(true, response);
            } else {
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(EntryInfo... params) {
            EntryInfo entryInfo = params[0];
            response = Apis.getApisInstance(mContext).doEntry(
                    CommonUtility.getJsonArray(String.valueOf(entryInfo.getAmount()),
                            entryInfo.getDebitAccount(), mContext, entryInfo.getGroupId()),
                    CommonUtility.getJsonArray(String.valueOf(entryInfo.getAmount()),
                            entryInfo.getCreditAccount(), mContext, entryInfo.getGroupId()),
                    entryInfo.getTripId(), entryInfo.getDescription(), entryInfo.getDate(), entryInfo.getTransactionType());
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        //    jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
//                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        jochild = japarent.getJSONObject(0);
                        entryInfo.setEntryId(jochild.getString(VariableClass.ResponseVariables.ENTRY_ID));
                        userService.fillnullentry(entryInfo.getId(), entryInfo.getEntryId());
                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = mContext.getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = mContext.getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

    public static void fetchInbox(Context ctx) {
        new ParseSms(ctx).execute();
    }

    private static ArrayList<SmsDto> parsevalues(ArrayList<SmsDto> body_val) {
        ArrayList<SmsDto> resSms = new ArrayList<>();
        for (int i = 0; i < body_val.size(); i++) {
            SmsDto smsDto = body_val.get(i);
            Pattern regEx
                    = Pattern.compile("(?:inr|rs)+[\\s]*[0-9+[\\,]*+[0-9]*]+[\\.]*[0-9]+");
            // Find instance of pattern matches
            Matcher m = regEx.matcher(smsDto.getBody());
            if (m.find()) {
                try {
                    Log.e("amount_value= ", "" + m.group(0));
                    String amount = (m.group(0).replaceAll("inr", ""));
                    amount = amount.replaceAll("rs", "");
                    amount = amount.replaceAll("inr", "");
                    amount = amount.replaceAll(" ", "");
                    amount = amount.replaceAll(",", "");
                    smsDto.setAmount(Double.valueOf(amount));
                    if (smsDto.getBody().contains("debited") ||
                            smsDto.getBody().contains("purchasing") || smsDto.getBody().contains("purchase") || smsDto.getBody().contains("dr")) {
                        smsDto.setTransactionType("0");
                    } else if (smsDto.getBody().contains("credited") || smsDto.getBody().contains("cr")) {
                        smsDto.setTransactionType("1");
                    }
                    smsDto.setParsed("1");
                    Log.e("matchedValue= ", "" + amount);
                    if (!Character.isDigit(smsDto.getSenderid().charAt(0)))
                        resSms.add(smsDto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("No_matchedValue ", "No_matchedValue ");
            }
        }
        return resSms;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public static Bitmap drawImageGrid(String text, Activity ctx) {
        int colorsarray[] = {ctx.getResources().getColor(R.color.orange_default_color)};
        Bitmap image = null;
        Paint paint = new Paint();
        paint.setTextSize(55);
        // Random rnd = new Random();
        //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        paint.setColor(ctx.getResources().getColor(android.R.color.white));
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        paint.setTextAlign(Paint.Align.CENTER);
        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/fontawesome_webfont.ttf");
        paint.setTypeface(tf);
        String temp;
        try {
            temp = String.valueOf(text.charAt(0)).toUpperCase();
        } catch (Exception e) {
            temp = "NA";
        }
        int width = ctx.getResources().getDimensionPixelOffset(R.dimen.image_height);
        //float baseline = width - (width / 4); // ascent() is negative
        int height = ctx.getResources().getDimensionPixelOffset(R.dimen.image_height);
        image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(colorsarray[new Random().nextInt(colorsarray.length)]);
        final Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
        canvas.drawBitmap(image, rect, rect, paint);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 1.5));
        // int yPos =  (canvas.getHeight() / 2);
        canvas.drawText(temp, xPos, yPos, paint);
        return getCroppedBitmap(image);
    }

    public static Bitmap drawImage(String text, Activity ctx) {
        int colorsarray[] = {ctx.getResources().getColor(R.color.white)};
        Bitmap image = null;
        Paint paint = new Paint();
        paint.setTextSize(30);
        // Random rnd = new Random();
        //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        paint.setColor(ctx.getResources().getColor(android.R.color.black));
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.
        paint.setTextAlign(Paint.Align.CENTER);
        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/gotham_light.ttf");
        paint.setTypeface(tf);
        String temp;
        try {
            if (text.contains(" ")) {
                temp = getInitials(text);
            } else {
                temp = text;
            }
        } catch (Exception e) {
            temp = "NA";
        }
        int width = ctx.getResources().getDimensionPixelOffset(R.dimen.image_height);
        //float baseline = width - (width / 4); // ascent() is negative
        int height = ctx.getResources().getDimensionPixelOffset(R.dimen.image_height);
        image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(colorsarray[new Random().nextInt(colorsarray.length)]);
        final Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
        canvas.drawBitmap(image, rect, rect, paint);
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(temp, xPos, yPos, paint);
        return getCroppedBitmap(image);
    }

    public static String getInitials(String str) {
        String initial = "";
        String[] split = str.split(" ");
        for (String value : split) {
            initial += value.substring(0, 1);
        }
        return initial;
    }

    public static SpannableString getfonttext(String txt, Activity ctx) {
        SpannableString s = new SpannableString(" " + txt);
        s.setSpan(new TypefaceSpan(ctx, "gotham_light.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static void expand(final View v) {
        Boolean b = false;
        if (v == null) {
            b = true;
        }
        Log.e("check if view is null", "" + b);
        v.measure(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ActionBar.LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    /**
     * Logout From Facebook
     */
    public static void callFacebookLogout(Context context) {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (!session.isClosed()) {
                session.closeAndClearTokenInformation();
            }
        }
    }

    public static SuperToast showCustomAlertForContactsError(Context ctx, String text) {
        SuperToast superToast = new SuperToast(ctx);
        superToast.setDuration(SuperToast.Duration.VERY_SHORT);
        superToast.setText(text);
        superToast.setBackground(R.color.blue_mat);
        superToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);
        superToast.show();
        return superToast;
    }

    static class ParseSms extends AsyncTask<Void, Void, Void> {
        Context mContext;
        UserService userService;
        EntryInfo entryInfo;

        public ParseSms(Context context) {
            mContext = context;
            userService = UserService.getUserServiceInstance(mContext);
        }

        @Override
        protected void onPostExecute(Void result) {
            if (CommonUtility.dialog != null)
                CommonUtility.dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(mContext);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor cursor = mContext.getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);
            cursor.moveToFirst();
            ArrayList<SmsDto> msgs = new ArrayList<>();
            while (cursor.moveToNext()) {
                String address = cursor.getString(1);
                String body = cursor.getString(3);
                Long smsid = cursor.getLong(0);
                Date date = new Date(cursor.getLong(cursor.getColumnIndex("date")));
                String formateddate = (new SimpleDateFormat("yyyy-MM-dd").format(date));
                long time = date.getTime();
                body = body.toLowerCase();
                SmsDto smsDto = new SmsDto();
                smsDto.setTime(time);
                Log.e("smsId", "" + smsid);
                smsDto.setSmsId(String.valueOf(smsid));
                smsDto.setBody(body);
                smsDto.setDate(formateddate);
                if (address.contains("-")) {
                    Log.e("senderid", address);
                    String[] parts = address.split("-");
                    smsDto.setSenderid(parts[1].toLowerCase());
                    msgs.add(smsDto);
                }
            }
            ArrayList<SmsDto> transactions = parsevalues(msgs);
            // checkAccounts(transactions, ctx);
            if (transactions.size() > 0) {
                for (int i = 0; i < transactions.size(); i++) {
                    SmsDto smsDto = transactions.get(i);
                    if (smsDto.getTransactionType() != null) {
                        Accounts accounts = new Accounts();
                        accounts.setAccountName(smsDto.getSenderid());
                        int id = userService.getmaxaccId() + 1;
                        accounts.setAcc_webId(String.valueOf(id));
                        accounts.setGroupId("3");
                        accounts.setOpeningBalance(0);
                        userService.addaccountsdata(accounts);
                    }
                }
                for (int i = 0; i < transactions.size(); i++) {
                    SmsDto smsDto = transactions.get(i);
                    if (smsDto.getTransactionType() != null) {
                        userService.addsms(smsDto);
                        entryInfo = new EntryInfo();
                        entryInfo.setDate(smsDto.getDate());
                        entryInfo.setAmount(smsDto.getAmount());
                        entryInfo.setTransactionType(smsDto.getTransactionType());
                        if (entryInfo.getTransactionType().equals("0")) {
                            entryInfo.setDebitAccount(userService.getinfoExpacc("Cash", "3").getAcc_webId());
                            entryInfo.setCreditAccount(userService.getinfoExpacc(smsDto.getSenderid(), "3").getAcc_webId());
                        } else {
                            entryInfo.setCreditAccount(userService.getinfoExpacc("Other", "1").getAcc_webId());
                            entryInfo.setDebitAccount(userService.getinfoExpacc(smsDto.getSenderid(), "3").getAcc_webId());
                        }
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String datesync = userService.getmaxentryin_date();
                            // Date datesms = (smsDto.getDate());
                            if (datesync == null)
                                userService.addentrydata(entryInfo);
                            else if (sdf.parse(smsDto.getDate()).after(sdf.parse(datesync))) {
                                userService.addentrydata(entryInfo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }
    }
}


