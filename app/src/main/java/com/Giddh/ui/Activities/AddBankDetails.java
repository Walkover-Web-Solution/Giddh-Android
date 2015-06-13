package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.AddBankAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.util.UserService;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddBankDetails extends AppCompatActivity {
    EditText openingbal, bankName;
    ListView listviewbanks;
    ImageButton save;
    ArrayList<Accounts> accounts;
    UserService userService;
    Context ctx;
    TextView addnew;
    Accounts accounts1;
    AddBankAdapter addBankAdapter;
    Boolean Bankacc = false;
    Boolean editmode = false;
    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = AddBankDetails.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        setContentView(R.layout.add_bank_details);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        userService = UserService.getUserServiceInstance(ctx);
        openingbal = (EditText) findViewById(R.id.opening_bal);
        bankName = (EditText) findViewById(R.id.bank_name);
        listviewbanks = (ListView) findViewById(R.id.banks);
        save = (ImageButton) findViewById(R.id.save);
        addnew = (TextView) findViewById(R.id.addnew);
        accounts = new ArrayList<>();
        Bankacc = getIntent().getExtras().getBoolean("value");
        if (Bankacc) {
            accounts = userService.getcountacc("3", "Cash");
            addBankAdapter = new AddBankAdapter(accounts, ctx);
            listviewbanks.setAdapter(addBankAdapter);
            addnew.setText("Add new Bank account");
            actionBar.setTitle(CommonUtility.getfonttext("Bank account", AddBankDetails.this));
        } else {
            accounts = userService.getcountacc("2", "Loan");
            addBankAdapter = new AddBankAdapter(accounts, ctx);
            listviewbanks.setAdapter(addBankAdapter);
            addnew.setText("Add new Credit card");
            actionBar.setTitle(CommonUtility.getfonttext("Credit card", AddBankDetails.this));
            bankName.setHint("Enter Credit card Name");
        }
        listviewbanks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                accounts1 = (Accounts) parent.getItemAtPosition(position);
                bankName.setText(accounts1.getAccountName());
                int bal = (int) accounts1.getOpeningBalance();
                openingbal.setText("" + bal);
                editmode = true;
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openingbal.getText().toString() != null && bankName.getText() != null) {
                    if (!editmode) {
                        if (Bankacc) {
                            if (!openingbal.getText().toString().equals("") && !bankName.getText().equals("")) {
                                Accounts accounts1 = new Accounts();
                                int id = userService.getmaxaccId() + 1;
                                accounts1.setAcc_webId(String.valueOf(id));
                                accounts1.setAccountName(bankName.getText().toString());
                                accounts1.setGroupId("3");
                                accounts1.setOpeningBalance(Double.parseDouble(openingbal.getText().toString()));
                                new AddAccount().execute(accounts1);
                                userService.addaccountsdata(accounts1);
                                accounts = userService.getcountacc("3", "Cash");
                                addBankAdapter = new AddBankAdapter(accounts, ctx);
                                listviewbanks.setAdapter(addBankAdapter);
                                openingbal.setText("");
                                bankName.setText("");
                                CommonUtility.showCustomAlertForContactsError(ctx, "Bank account added");
                            } else {
                                CommonUtility.showCustomAlertForContactsError(ctx, "Field missing");
                            }
                        } else {
                            if (!openingbal.getText().toString().equals("") && !bankName.getText().equals("")) {
                                Accounts accounts1 = new Accounts();
                                int id = userService.getmaxaccId() + 1;
                                accounts1.setAcc_webId(String.valueOf(id));
                                accounts1.setAccountName(bankName.getText().toString());
                                accounts1.setGroupId("2");
                                accounts1.setOpeningBalance(Double.parseDouble(openingbal.getText().toString()));
                                new AddAccount().execute(accounts1);
                                userService.addaccountsdata(accounts1);
                                accounts = userService.getcountacc("2", "Loan");
                                addBankAdapter = new AddBankAdapter(accounts, ctx);
                                listviewbanks.setAdapter(addBankAdapter);
                                openingbal.setText("");
                                bankName.setText("");
                                CommonUtility.showCustomAlertForContactsError(ctx, "Credit card added");
                            } else {
                                CommonUtility.showCustomAlertForContactsError(ctx, "Field missing");
                            }
                        }
                    } else {
                        if (!openingbal.getText().toString().equals("") && !bankName.getText().equals("")) {
                            editmode = false;
                            accounts1.setOpeningBalance(Double.parseDouble(openingbal.getText().toString()));
                            accounts1.setAccountName(bankName.getText().toString());
                            userService.updateaccount(accounts1);
                            openingbal.setText("");
                            bankName.setText("");
                            listviewbanks.setAdapter(addBankAdapter);
                            CommonUtility.showCustomAlertForContactsError(ctx, "Updated successfully");
                        } else {
                            CommonUtility.showCustomAlertForContactsError(ctx, "Field missing");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ctx, SettingsPage.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        AddBankDetails.this.finish();
    }

    class AddAccount extends AsyncTask<Accounts, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(ctx);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Accounts... params) {
            Accounts paramAcc = params[0];
            response = Apis.getApisInstance(ctx).addAccount(CommonUtility.getgroupIdName
                    (paramAcc.getGroupId()), paramAcc.getAccountName(), null, String.valueOf(paramAcc.getOpeningBalance()));
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
}
