package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.FlagTypAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.TripInfo;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsPage extends AppCompatActivity implements View.OnClickListener {
    TextView tvTrips, tvBankAccounts, tvSyncEntries, tvProfileName, tvCreditCard,
            tvCurrency, tvaccounts, subaccmain,
            subcurrency, subSync, subtrips, subaccounts, subprofilename, subcard;
    Context ctx;
    UserService userService;
    RelativeLayout accounts1, logout1;
    int count;
    ArrayList<Accounts> accounts, cat;
    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        ctx = SettingsPage.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        accounts = new ArrayList<>();
        cat = new ArrayList<>();
        userService = UserService.getUserServiceInstance(ctx);
        actionBar.setTitle(CommonUtility.getfonttext("Settings", SettingsPage.this));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        tvTrips = (TextView) findViewById(R.id.aso_trip);
        tvBankAccounts = (TextView) findViewById(R.id.add_bnk);
        tvCreditCard = (TextView) findViewById(R.id.credit_card);
        tvCurrency = (TextView) findViewById(R.id.currency);
        tvCurrency.setOnClickListener(this);
        subcurrency = (TextView) findViewById(R.id.subcurrency);
        tvaccounts = (TextView) findViewById(R.id.accounts);
        subaccmain = (TextView) findViewById(R.id.subaccount);
        accounts1 = (RelativeLayout) findViewById(R.id.accounts1);
        logout1 = (RelativeLayout) findViewById(R.id.logout1);
        subaccmain.setText("" + getfilteredlist().size());
        accounts1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trip = new Intent(ctx, SelectInfo.class);
                trip.putExtra(VariableClass.Vari.SELECTEDDATA, 4);
                startActivity(trip);
                SettingsPage.this.finish();
            }
        });
        logout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialogWrapper.Builder(ctx)
                        .setTitle("Are you sure?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtility.clearData(ctx, SettingsPage.this);
                        VariableClass.Vari.LOGIN_FIRST_TIME = true;
                        VariableClass.Vari.MESSEGE_FIRST_TIME = true;
                        Intent SignUp = new Intent(ctx, SplashScreen.class);
                        SignUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SignUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(SignUp);
                        SettingsPage.this.finish();
                    }
                }).show();
            }
        });
        tvSyncEntries = (TextView) findViewById(R.id.sync);
        subSync = (TextView) findViewById(R.id.subSync);
        tvProfileName = (TextView) findViewById(R.id.change_name);
        subSync.setText("Last updated: " + Prefs.getUpdateDate(ctx));
        subprofilename = (TextView) findViewById(R.id.subprofilename);
        subprofilename.setText(Prefs.getEmailId(ctx));
        subaccounts = (TextView) findViewById(R.id.subaccounts);
        tvSyncEntries.setOnClickListener(this);
        tvBankAccounts.setOnClickListener(this);
        tvTrips.setOnClickListener(this);
        tvCreditCard.setOnClickListener(this);
        tvProfileName.setOnClickListener(this);
        accounts = userService.getcountacc("3", "Cash");
        subaccounts.setText("" + accounts.size());
        subcard = (TextView) findViewById(R.id.subcard);
        accounts = userService.getcountacc("2", "Loan");
        subcard.setText("" + accounts.size());
        subtrips = (TextView) findViewById(R.id.subtrips);
        count = userService.getcount("trip_info");
        subtrips.setText("" + count);
        tvCurrency.setText(Prefs.getCurrency(ctx));
        subcurrency.setText(Prefs.getCountry(ctx));
        tvProfileName.setText(Prefs.getUserName(ctx));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aso_trip:
                Intent multi = new Intent(ctx, SavedTrips.class);
                startActivity(multi);
                break;
            case R.id.add_bnk:
                Intent addbank = new Intent(ctx, AddBankDetails.class);
                addbank.putExtra("value", true);
                startActivity(addbank);
                break;
            case R.id.sync:
                CommonUtility.syncwithServer(ctx);
                CommonUtility.showCustomAlertForContactsError(ctx, "Updated");
                break;
            case R.id.credit_card:
                Intent addcard = new Intent(ctx, AddBankDetails.class);
                addcard.putExtra("value", false);
                startActivity(addcard);
                break;
            case R.id.change_name:
                new MaterialDialog.Builder(ctx)
                        .title("Enter new profile Name")
                        .content("Profile Name will be changed").negativeText("CANCEL").positiveText("OK")
                        .input("ex:John Martin", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.toString() != null && !input.toString().equals("")) {
                                    new ChangeProfileName().execute(input.toString());
                                    Prefs.setUserName(ctx, input.toString());
                                    tvProfileName.setText(Prefs.getUserName(ctx));
                                    subprofilename.setText(Prefs.getEmailId(ctx));
                                }
                            }
                        }).show();
                break;
            case R.id.currency:
                String[] country = getResources().getStringArray(R.array.country_list);
                String[] symbol = getResources().getStringArray(R.array.country_symbol);
                String mixarr[] = new String[country.length];
                for (int i = 0; i < country.length; i++) {
                    String both = country[i] + " (" + symbol[i] + ")";
                    mixarr[i] = both;
                }
                new MaterialDialog.Builder(ctx)
                        .title("Select Currency")
                        .items(mixarr)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text != null) {
                                    tvCurrency.setText(text.toString().substring(text.toString().indexOf("(") + 1, text.toString().indexOf(")")));
                                    Prefs.setCurrency(ctx, text.toString().substring(text.toString().indexOf("(") + 1, text.toString().indexOf(")")));
                                    Prefs.setCountry(ctx, text.toString().substring(0, text.toString().indexOf(" ")));
                                    subcurrency.setText(text.toString().substring(0, text.toString().indexOf(" ")));
                                }
                                return true;
                            }
                        })
                        .positiveText("Ok")
                        .show();
                break;
        }
    }

    class ChangeProfileName extends AsyncTask<String, Void, Void> {
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
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            response = Apis.getApisInstance(ctx).changeProfileName(params[0]);
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

    ArrayList<Accounts> getfilteredlist() {
        cat = userService.getallAccounts("3", false, true);
        ArrayList<Accounts> temp = new ArrayList<>();
        String[] accNames = getResources().getStringArray(R.array.accountName);
        int listsize = cat.size();
        for (int i = 0; i < listsize; i++) {
            if (!Arrays.asList(accNames).contains(cat.get(i)
                    .getAccountName())) {
                temp.add(cat.get(i));
            }
        }
        return temp;
    }
}
