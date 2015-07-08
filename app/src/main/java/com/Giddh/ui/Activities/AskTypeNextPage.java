package com.Giddh.ui.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.Giddh.R;
import com.Giddh.adapters.FlagTypAdapter;
import com.Giddh.adapters.TripGridAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.TripInfo;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.MaterialDialog;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AskTypeNextPage extends Activity {
    GridView grid_medium, tripsAssociated;
    EditText edDescription;
    Context ctx;
    UserService userService;
    ArrayList<Accounts> fromMedium;
    ArrayList<TripInfo> tripAsso;
    FlagTypAdapter flagTypeAdapter;
    TripGridAdapter tripAssoAdapter;
    TripInfo tripInfo;
    Accounts accounts;
    ImageButton ibSave, ibCancel;
    EntryInfo entryInfo;
    View previousitemview, previousitemviewtrip;
    Boolean colored;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asktype_nextpage);
        ctx = AskTypeNextPage.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        entryInfo = (EntryInfo) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        grid_medium = (GridView) findViewById(R.id.medium_trans);
        tripsAssociated = (GridView) findViewById(R.id.trips_saved);
        edDescription = (EditText) findViewById(R.id.description);
        ibSave = (ImageButton) findViewById(R.id.save);
        ibCancel = (ImageButton) findViewById(R.id.cancel);
        userService = UserService.getUserServiceInstance(ctx);
        fromMedium = new ArrayList<>();
        tripAsso = new ArrayList<>();
        fromMedium = userService.getallAccounts("3", false, false);
        tripAsso = userService.getallTripInfo(null, true);
        if (entryInfo.getAtm()) {
            ArrayList<Accounts> cashonly = new ArrayList<>();
            for (int i = 0; i < fromMedium.size(); i++) {
                if (fromMedium.get(i).getAccountName().equals("Cash"))
                    cashonly.add(fromMedium.get(i));
            }
            flagTypeAdapter = new FlagTypAdapter(cashonly, ctx, AskTypeNextPage.this, true);
        } else
            flagTypeAdapter = new FlagTypAdapter(fromMedium, ctx, AskTypeNextPage.this, false);
        tripAssoAdapter = new TripGridAdapter(tripAsso, ctx, AskTypeNextPage.this);
        grid_medium.setAdapter(flagTypeAdapter);
        tripsAssociated.setAdapter(tripAssoAdapter);
        grid_medium.setDrawSelectorOnTop(false);
        grid_medium.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
            }
        });
        grid_medium.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previousitemview == view && colored) {
                    (view.findViewById(R.id.tick_image)).setVisibility(View.GONE);
                    view.setBackgroundColor(getResources().getColor(R.color.transparent));
                    colored = false;
                    entryInfo.setSelected(false);
                } else {
                    (view.findViewById(R.id.tick_image)).setVisibility(View.VISIBLE);
                    view.setBackgroundColor(getResources().getColor(R.color.light_bg));
                    colored = true;
                    entryInfo.setSelected(true);
                }
                if (previousitemview != null && previousitemview != view) {
                    previousitemview.setBackgroundColor(getResources().getColor(R.color.transparent));
                    (previousitemview.findViewById(R.id.tick_image)).setVisibility(View.GONE);
                }

                previousitemview = view;
                accounts = (Accounts) parent.getItemAtPosition(position);
                if (entryInfo.getTransactionType().equals("0")) {
                    entryInfo.setDebitAccount(String.valueOf(accounts.getAcc_webId()));
                } else {
                    entryInfo.setCreditAccount(String.valueOf(accounts.getAcc_webId()));
                }
            }
        });
        tripsAssociated.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previousitemviewtrip == view && colored) {
                    (view.findViewById(R.id.tick_image)).setVisibility(View.GONE);
                    view.setBackgroundColor(getResources().getColor(R.color.transparent));
                    colored = false;
                    entryInfo.setTripId("");
                } else {
                    (view.findViewById(R.id.tick_image)).setVisibility(View.VISIBLE);
                    view.setBackgroundColor(getResources().getColor(R.color.light_bg));
                    colored = true;
                    tripInfo = (TripInfo) parent.getItemAtPosition(position);
                    entryInfo.setTripId(tripInfo.getTripId());
                }
                if (previousitemviewtrip != null && previousitemviewtrip != view) {
                    previousitemviewtrip.setBackgroundColor(getResources().getColor(R.color.transparent));
                    (previousitemviewtrip.findViewById(R.id.tick_image)).setVisibility(View.GONE);
                }
                previousitemviewtrip = view;
            }
        });
        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((entryInfo.getDebitAccount() != null)
                        && (entryInfo.getCreditAccount() != null) && entryInfo.getSelected()) {
                    ibSave.setEnabled(false);
                    CommonUtility.show_PDialog(ctx);
                    CommonUtility.syncwithServer(ctx);
                    if (!edDescription.getText().toString().equals("") &&
                            edDescription.getText().toString() != null) {
                        entryInfo.setDescription(edDescription.getText().toString());
                    }
                    if (CommonUtility.isNetworkAvailable(ctx)) {
                        new DoEntry().execute();
                    } else {
                        new BtnSave().execute();
                    }
                } else {
                    CommonUtility.showCustomAlertForContactsError(ctx, "Please select category first");
                }
            }
        });
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancel = new Intent(ctx, AskType.class);
                startActivity(cancel);
                AskTypeNextPage.this.finish();
            }
        });
    }

    class DoEntry extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            new BtnSave().execute();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).doEntry(
                    CommonUtility.getJsonArray(String.valueOf(entryInfo.getAmount()),
                            entryInfo.getDebitAccount(), ctx, entryInfo.getGroupId()),
                    CommonUtility.getJsonArray(String.valueOf(entryInfo.getAmount()),
                            entryInfo.getCreditAccount(), ctx, entryInfo.getGroupId()),
                    entryInfo.getTripId(), entryInfo.getDescription(), entryInfo.getDate(), entryInfo.getTransactionType());
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    String s = joparent.getString(VariableClass.ResponseVariables.RESPONSE);
                    if (s.equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (s.equals(Apis.SuccessResponse)) {
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        entryInfo.setEntryId(japarent.getJSONObject(0).getString(VariableClass.ResponseVariables.ENTRY_ID));
                        Log.e("EntryId from server", entryInfo.getEntryId());
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

    class BtnSave extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            CommonUtility.showCustomAlertForContactsError(ctx, "Entry Added");
            Intent menu = new Intent(ctx, AskType.class);
            menu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            menu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(menu);
            // AskTypeNextPage.this.finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            userService.addentrydata(entryInfo);
            if (entryInfo.getTripId() != null && !entryInfo.getTripId().equals("")) {
                entryInfo.setEmail(Prefs.getEmailId(ctx));
                userService.addTripentrydata(entryInfo);
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
            CommonUtility.dialog.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
            CommonUtility.dialog.cancel();
        }
    }
}

