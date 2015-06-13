package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.Giddh.R;
import com.Giddh.adapters.FlagTypAdapter;
import com.Giddh.adapters.TripGridAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.TripInfo;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectInfo extends AppCompatActivity {
    Context ctx;
    GridView info;
    FlagTypAdapter flagTypeAdapter;
    ArrayList<Accounts> cat = null;
    UserService userService;
    ArrayList<TripInfo> tripAsso;
    TripGridAdapter tripAssoAdapter;
    int paramtype;
    Accounts acval;
    TripInfo tripInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Calledinfo", "Calledinfo");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_info);
        ctx = SelectInfo.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        init();
    }

    void init() {
        info = (GridView) findViewById(R.id.flags);
        userService = UserService.getUserServiceInstance(ctx);
        cat = new ArrayList<>();
        tripAsso = new ArrayList<>();
        paramtype = getIntent().getExtras().getInt(VariableClass.Vari.SELECTEDDATA);
        switch (paramtype) {
            case 0:
                setgridAdapter("0");
                break;
            case 1:
                setgridAdapter("1");
                break;
            case 2:
                tripAsso = userService.getallTripInfo(null, true);
                tripAssoAdapter = new TripGridAdapter(tripAsso, ctx, SelectInfo.this);
                info.setAdapter(tripAssoAdapter);
                break;
            case 3:
                cat = userService.getallAccounts("3", false, false);
                flagTypeAdapter = new FlagTypAdapter(cat, ctx, SelectInfo.this, false);
                info.setAdapter(flagTypeAdapter);
                break;
            case 4:
                flagTypeAdapter = new FlagTypAdapter(getfilteredlist(), ctx, SelectInfo.this, false);
                info.setAdapter(flagTypeAdapter);
                break;
        }
        info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (paramtype == 0 || paramtype == 1 || paramtype == 3) {
                    acval = (Accounts) parent.getItemAtPosition(position);
                    SelectInfo.this.finish();
                } else if (paramtype == 2) {
                    tripInfo = (TripInfo) parent.getItemAtPosition(position);
                    SelectInfo.this.finish();
                }
            }
        });
        info.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Accounts accounts = (Accounts) parent.getItemAtPosition(position);
                if (paramtype == 4) {
                    new AlertDialogWrapper.Builder(ctx)
                            .setTitle("Remove " + accounts.getAccountName() + "?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (userService.getEntryInfo(accounts.getAcc_webId()).size() == 0) {
                                new DeleteAccountFromServer().execute(accounts.getAccountName(), CommonUtility.getgroupIdName(accounts.getGroupId()));
                                userService.delete_account(accounts.getAcc_webId());
                                flagTypeAdapter = new FlagTypAdapter(getfilteredlist(), ctx, SelectInfo.this, false);
                                info.setAdapter(flagTypeAdapter);
                            } else {
                                CommonUtility.showCustomAlertForContactsError(ctx, accounts.getAccountName() + " is in use");
                            }
                        }
                    }).show();
                }
                return true;
            }
        });
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

    void setgridAdapter(String id) {
        cat = userService.getallAccounts(id, true, false);
        flagTypeAdapter = new FlagTypAdapter(cat, ctx, SelectInfo.this, false);
        info.setAdapter(flagTypeAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (paramtype == 4) {
            Intent settings = new Intent(ctx, SettingsPage.class);
            startActivity(settings);
            SelectInfo.this.finish();
        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        if (paramtype == 0 || paramtype == 1 || paramtype == 3) {
            data.putExtra(VariableClass.Vari.SELECTEDDATA, acval);
        } else {
            data.putExtra(VariableClass.Vari.SELECTEDDATA, tripInfo);
        }
        setResult(RESULT_OK, data);
        super.finish();
    }

    class DeleteAccountFromServer extends AsyncTask<String, Void, Void> {
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
            CommonUtility.show_PDialog(ctx);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            response = Apis.getApisInstance(ctx).deleteAccount(params[0], params[1]);
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                //JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
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
