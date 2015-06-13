
package com.Giddh.ui.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.FlagTypAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.AccountDetails;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.CompanyDetails;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.GroupDetails;
import com.Giddh.dtos.TripInfo;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AskType extends Activity {
    RelativeLayout receivingMoney, givingMoney, askMoneyroot, flagLayout;
    Context context;
    TextView selectDate, currency;
    TripInfo tripInfo;
    Animation slide_down, slide_up, slide_back_up, slide_back_down;
    GridView gvType_tags;
    EditText enteredAmount;
    UserService userService;
    FlagTypAdapter flagTypeAdapter;
    private DatePickerDialog pickdate;
    private SimpleDateFormat dateFormatter;
    ArrayList<Accounts> cat = null;
    ArrayList<TripInfo> savedTrips;
    ArrayList<TripInfo> tripsInDb;
    EntryInfo entryInfo;
    static Boolean receiving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_type);
        context = AskType.this;
        Mint.initAndStartSession(context, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(context));
        receivingMoney = (RelativeLayout) findViewById(R.id.receving_money);
        givingMoney = (RelativeLayout) findViewById(R.id.giving_money);
        askMoneyroot = (RelativeLayout) findViewById(R.id.ask_money);
        flagLayout = (RelativeLayout) findViewById(R.id.flag_layout);
        selectDate = (TextView) findViewById(R.id.select_date);
        selectDate.setPaintFlags(selectDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        gvType_tags = (GridView) findViewById(R.id.flags);
        enteredAmount = (EditText) findViewById(R.id.amount);
        currency = (TextView) findViewById(R.id.sign);
        currency.setText(Prefs.getCurrency(context));
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        cat = new ArrayList<>();
        userService = UserService.getUserServiceInstance(context);
        savedTrips = new ArrayList<>();
        tripsInDb = new ArrayList<>();
        if (CommonUtility.isNetworkAvailable(context)) {
            new AccountList().execute();
            new tripList().execute();
        }
        slide_down = AnimationUtils.loadAnimation(this,
                R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(this,
                R.anim.slide_up);
        slide_back_up = AnimationUtils.loadAnimation(context,
                R.anim.slide_back_up);
        slide_back_down = AnimationUtils.loadAnimation(context,
                R.anim.slide_back_down);
        receivingMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiving = true;
                givingMoney.startAnimation(slide_back_up);
                givingMoney.setVisibility(View.GONE);
                receivingMoney.startAnimation(slide_back_down);
                receivingMoney.setVisibility(View.GONE);
                askMoneyroot.setVisibility(View.GONE);
                flagLayout.setVisibility(View.VISIBLE);
                setgridAdapter("0");
            }
        });
        setDateTimeField();
        gvType_tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Accounts accounts;
                entryInfo = new EntryInfo();
                //entryInfo.setDebitAccount(parent.get);
                accounts = (Accounts) parent.getItemAtPosition(position);
                if (accounts.getAccountName().equalsIgnoreCase("Other")) {
                    new MaterialDialog.Builder(context)
                            .title("Enter category name")
                            .content("this category will be added in your list")
                            .input("Food", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    if (!input.toString().equals("") && input.toString() != null) {
                                        if (!receiving) {
                                            Accounts acdto = new Accounts();
                                            acdto.setAccountName(input.toString());
                                            acdto.setOpeningBalance(0);
                                            int id = userService.getmaxaccId() + 1;
                                            acdto.setAcc_webId(String.valueOf(id));
                                            acdto.setGroupId("1");
                                            userService.addaccountsdata(acdto);
                                            Log.e("WebIDafter", "" + userService.getmaxaccId());
                                            setgridAdapter("1");
                                        } else {
                                            Accounts acdto = new Accounts();
                                            acdto.setAccountName(input.toString());
                                            acdto.setGroupId("0");
                                            int id = userService.getmaxaccId() + 1;
                                            acdto.setAcc_webId(String.valueOf(id));
                                            Log.e("WebID", "" + userService.getmaxaccId());
                                            userService.addaccountsdata(acdto);
                                            setgridAdapter("0");
                                        }
                                    }
                                }
                            }).show();
                } else if (!enteredAmount.getText().toString().equals("") && enteredAmount.getText().toString() != null && Double.valueOf(enteredAmount.getText().toString()) > 0) {
                    entryInfo.setCompanyId(Prefs.getCompanyId(context));
                    entryInfo.setDate(selectDate.getText().toString());
                    if (receiving) {
                        entryInfo.setCreditAccount(String.valueOf(accounts.getAcc_webId()));
                        entryInfo.setTransactionType("0");
                        entryInfo.setAtm(false);
                    } else {
                        entryInfo.setDebitAccount(String.valueOf(accounts.getAcc_webId()));
                        entryInfo.setTransactionType("1");
                        entryInfo.setAtm(false);
                    }
                    if (accounts.getGroupId().equals("2")) {
                        new MaterialDialog.Builder(context)
                                .title("Enter email")
                                .content("email will be saved for future")
                                .input("ex:john@gmail.com", "", new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(MaterialDialog dialog, CharSequence input) {
                                        entryInfo.setAmount(Double.parseDouble(enteredAmount.getText().toString()));
                                        entryInfo.setEmailloan(input.toString());
                                        Intent intent = new Intent(context, AskTypeNextPage.class);
                                        intent.putExtra(VariableClass.Vari.SELECTEDDATA, entryInfo);
                                        startActivity(intent);
                                    }
                                }).show();
                    } else if (accounts.getAccountName().equalsIgnoreCase("ATM withdraw")) {
                        if (userService.getcountacc("3", "Cash").size() > 0) {
                            ArrayList<Accounts> banks = new ArrayList<Accounts>();
                            banks = userService.getcountacc("3", "Cash");
                            String banksarr[] = new String[banks.size()];
                            if (banks.size() > 0)
                                for (int i = 0; i < banks.size(); i++) {
                                    banksarr[i] = banks.get(i).getAccountName();
                                }
                            new MaterialDialog.Builder(context)
                                    .title("Select Bank")
                                    .items(banksarr)
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            Log.e("bank_selected", "" + text);
                                            if (text != null && !text.equals("")) {
                                                entryInfo.setCreditAccount(String.valueOf(userService.getaccountnameorId(text.toString()).getAcc_webId()));
                                                entryInfo.setAmount(Double.parseDouble(enteredAmount.getText().toString()));
                                                entryInfo.setAtm(true);
                                                Intent intent = new Intent(context, AskTypeNextPage.class);
                                                intent.putExtra(VariableClass.Vari.SELECTEDDATA, entryInfo);
                                                startActivity(intent);
                                            }
                                            return true;
                                        }
                                    })
                                    .positiveText("Ok")
                                    .show();
                        } else {
                            Intent addbank = new Intent(context, AddBankDetails.class);
                            addbank.putExtra("value", true);
                            startActivity(addbank);
                        }
                    } else {
                        if (receiving) {
                            entryInfo.setCreditAccount(String.valueOf(accounts.getAcc_webId()));
                            entryInfo.setTransactionType("0");
                            entryInfo.setAtm(false);
                        } else {
                            entryInfo.setDebitAccount(String.valueOf(accounts.getAcc_webId()));
                            entryInfo.setTransactionType("1");
                            entryInfo.setAtm(false);
                        }
                        Log.e("AccountName", userService.getaccountnameorId(accounts.getAcc_webId()).getAccountName());
                        entryInfo.setAmount(Double.parseDouble(enteredAmount.getText().toString()));
                        Log.e("Amount_value", enteredAmount.getText().toString());
                        Intent intent = new Intent(context, AskTypeNextPage.class);
                        intent.putExtra(VariableClass.Vari.SELECTEDDATA, entryInfo);
                        startActivity(intent);
                    }
                } else {
                    CommonUtility.showCustomAlertForContactsError(context, "Enter valid amount first.");
                }
            }
        });
        givingMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiving = false;
                givingMoney.startAnimation(slide_back_up);
                givingMoney.setVisibility(View.GONE);
                receivingMoney.startAnimation(slide_back_down);
                receivingMoney.setVisibility(View.GONE);
                askMoneyroot.setVisibility(View.GONE);
                flagLayout.setVisibility(View.VISIBLE);
                // footerFlag.setBackgroundColor(getResources().getColor(R.color.orange_foot));
                setgridAdapter("1");
            }
        });
        final View actionB = findViewById(R.id.action_b);
        final View actionA = findViewById(R.id.action_a);
        final View actionC = findViewById(R.id.action_c);
        final View actionD = findViewById(R.id.action_d);
        actionB.setBackgroundResource(R.drawable.summary_icon);
        actionA.setBackgroundResource(R.drawable.add_trip);
        actionC.setBackgroundResource(R.drawable.setting_icon);
        actionD.setBackgroundResource(R.drawable.company);
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingsPage.class);
                startActivity(intent);
            }
        });
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SummaryInfo.class);
                startActivity(intent);
            }
        });
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userService.getallTripInfo(null, true).size() > 0) {
                    Intent multi = new Intent(context, SavedTrips.class);
                    startActivity(multi);
                } else {
                    Intent intent = new Intent(context, TripHome.class);
                    startActivity(intent);
                }
            }
        });
        actionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                AskType.this.finish();

            }
        });
        if (CommonUtility.isNetworkAvailable(context)) {
            CommonUtility.syncwithServer(context);
            new DelEntries().execute();
        }
    }

    @Override
    protected void onStart() {
        receivingMoney.startAnimation(slide_down);
        receivingMoney.setVisibility(View.VISIBLE);
        givingMoney.startAnimation(slide_up);
        givingMoney.setVisibility(View.VISIBLE);
        super.onStart();
    }

    private void setDateTimeField() {
        selectDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pickdate.show();
                return false;
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        selectDate.setText(dateFormatter.format(newCalendar.getTime()));
        pickdate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                selectDate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    void setgridAdapter(String id) {
        cat = userService.getallAccounts(id, true, false);
        for (int i = 0; i < cat.size(); i++) {
            Log.e("groupid and name", cat.get(i).getGroupId() + " " + cat.get(i).getAccountName() + "grp_webid" + cat.get(i).getAcc_webId());
        }
        flagTypeAdapter = new FlagTypAdapter(cat, context, AskType.this, false);
        gvType_tags.setAdapter(flagTypeAdapter);
    }

    class tripList extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;
        JSONArray japarent = null;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            if (iserr) {
                //showErrorMessage(true, response);
            } else {
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // showErrorMessage(false, "");
            CommonUtility.show_PDialog(context);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(context).getTripList();
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                //JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        userService.deleteAlltrips();
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        if (japarent.length() > 0)
                            for (int i = 0; i < japarent.length(); i++) {
                                tripInfo = new TripInfo();
                                jochild = japarent.getJSONObject(i);
                                tripInfo.setTripId(jochild.getString(VariableClass.ResponseVariables.TRIP_ID));
                                tripInfo.setOwner(jochild.getString(VariableClass.ResponseVariables.OWNER));
                                tripInfo.setTripName(jochild.getString(VariableClass.ResponseVariables.TRIP_NAME));
                                savedTrips.add(tripInfo);
                                tripsInDb = userService.getallTripInfo(tripInfo.getTripId(), false);
                                if (savedTrips != null)
                                    tripInfo.setSavedTrips(savedTrips);
                                if (tripsInDb.size() > 0) {
                                    userService.updateTripInfo(tripInfo, tripInfo.getTripId());
                                } else {
                                    userService.addTripInfo(tripInfo);
                                }
                            }
                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

    class DelEntries extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;
        JSONArray japarent = null;

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
            // showErrorMessage(false, "");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(context).getalldelete();
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                //JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        jochild = japarent.getJSONObject(0);
                        japarent = jochild.getJSONArray(VariableClass.ResponseVariables.ENTRY_ID);
                        for (int i = 0; i < japarent.length(); i++) {
                            userService.deleteEntry(false, japarent.getString(i));
                            userService.deleteEntry(true, japarent.getString(i));
                        }
                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

    class AccountList extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            // CommonUtility.dialog.dismiss();
            if (iserr) {
                //showErrorMessage(true, response);
            } else {
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // showErrorMessage(false, "");
            //CommonUtility.show_PDialog(context);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(context).getAccountList();
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray jachild = null;
                JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        jochild = japarent.getJSONObject(0);
                        CompanyDetails acc = new CompanyDetails();
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        acc = gson.fromJson(japarent.getJSONObject(0).toString(), CompanyDetails.class);
                        for (int i = 0; i < acc.getGroupDetail().size(); i++) {
                            GroupDetails groupDetails = acc.getGroupDetail().get(i);
                            String grpName = acc.getGroupDetail().get(i).getGroupName();
                            grpName = CommonUtility.getgroupIdName(grpName);
                            for (int j = 0; j < acc.getGroupDetail().get(i).getAccountDetails().size(); j++) {
                                Accounts accounts = new Accounts();
                                AccountDetails accountDetails = new AccountDetails();
                                accountDetails = acc.getGroupDetail().get(i).getAccountDetails().get(j);
                                accounts.setGroupId(grpName);
                                accounts.setAccountName(accountDetails.getAccountName());
                                userService.getmaxaccId();
                                int id = userService.getmaxaccId() + 1;
                                Log.e("web_id=", " " + id);
                                accounts.setAcc_webId(String.valueOf(id));
                                if (accountDetails.getOpeningBalance() != null && !accountDetails.getOpeningBalance().equals("")) {
                                    if (accountDetails.getOpeningBalanceType().equals("1")) {
                                        accounts.setOpeningBalance(Double.parseDouble(accountDetails.getOpeningBalance()) * -1);
                                    } else {
                                        accounts.setOpeningBalance(Double.parseDouble((accountDetails.getOpeningBalance())));
                                    }
                                } else {
                                    accounts.setOpeningBalance(0);
                                }
                                userService.addaccountsdata(accounts);
                            }
                        }
                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CommonUtility.dialog != null)
            CommonUtility.dialog.dismiss();
        new tripList().cancel(true);
    }

    @Override
    protected void onStop() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (givingMoney.getVisibility() == View.VISIBLE || receivingMoney.getVisibility() == View.VISIBLE) {
            Intent i = new Intent(context, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            AskType.this.finish();
        } else {
            Intent i = new Intent(context, AskType.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            AskType.this.finish();
        }
    }
}

