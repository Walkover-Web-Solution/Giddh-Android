package com.Giddh.ui.Activities;

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.SearchableAdapter;
import com.Giddh.adapters.TripEmailsAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.TripInfo;
import com.Giddh.dtos.TripShare;
import com.Giddh.dtos.UserEmail;
import com.Giddh.util.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.splunk.mint.Mint;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TripHome extends AppCompatActivity {
    UserEmail emailDto, mailAddDto;
    Context ctx;
    ListView lvEmail;
    EditText edTripName;
    AutoCompleteTextView emailAdd;
    ListView selected_emails;
    ArrayList<UserEmail> selectedMails;
    TripEmailsAdapter tripEmailsAdapter;
    ImageButton add_multiEmail;
    ArrayList<UserEmail> EmailList;
    SwipeActionAdapter swipeActionAdapter;
    Button btnGo;
    RelativeLayout error_layout;
    TextView error_FontTextView;
    TripInfo tripInfo, tripinfoedit;
    int index;
    static String sharedMail;
    UserService userService;
    SearchableAdapter searchableAdapter;
    TripShare tripShare;
    ArrayList<TripShare> trips;
    Boolean EditMode;
    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_trip);
        init();
        emailAdd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (tripinfoedit != null && EditMode) {
                    if (tripinfoedit.getOwner().equals("1")) {
                        selectedMails.add((UserEmail) parent.getItemAtPosition(position));
                        swipeActionAdapter = new SwipeActionAdapter(new TripEmailsAdapter(selectedMails, ctx));
                        swipeActionAdapter.setListView(selected_emails);
                        selected_emails.setAdapter(swipeActionAdapter);
                        initializeListner();
                        swipeActionAdapter.notifyDataSetChanged();
                        //selected_emails.setAdapter(new TripEmailsAdapter(selectedMails, ctx));
                        emailAdd.setText("");
                        CommonUtility.showCustomAlertForContactsError(ctx, "Email Added");
                    } else {
                        emailAdd.setText("");
                        CommonUtility.showCustomAlertForContactsError(ctx, "You don't have permissions");
                    }
                }
                if (EditMode == null && tripinfoedit == null) {
                    selectedMails.add((UserEmail) parent.getItemAtPosition(position));
                    swipeActionAdapter = new SwipeActionAdapter(new TripEmailsAdapter(selectedMails, ctx));
                    swipeActionAdapter.setListView(selected_emails);
                    selected_emails.setAdapter(swipeActionAdapter);
                    initializeListner();
                    swipeActionAdapter.notifyDataSetChanged();
                    //selected_emails.setAdapter(new TripEmailsAdapter(selectedMails, ctx));
                    emailAdd.setText("");
                    CommonUtility.showCustomAlertForContactsError(ctx, "Email Added");
                }
            }
        });
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edTripName.getText().toString() != null && !edTripName.getText().toString().equals("")) {
                    if (EditMode != null && tripinfoedit != null) {
                        new editTrip().execute();
                    } else {
                        if (EditMode == null && tripinfoedit == null)
                            try {
                                new creatTrip().execute().get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                    }
                    Intent finish = new Intent(ctx, AskType.class);
                    startActivity(finish);
                    TripHome.this.finish();
                } else {
                    CommonUtility.showCustomAlertForContactsError(ctx, "Enter trip name");
                }
            }
        });
        add_multiEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EditMode != null && EditMode) {
                    if (tripinfoedit.getOwner().equals("1") && emailDto != null) {
                        Intent multi = new Intent(ctx, SelectEmails.class);
                        multi.putExtra(VariableClass.Vari.SELECTEDDATA, emailDto);
                        startActivityForResult(multi, 1001);
                    } else {
                        CommonUtility.showCustomAlertForContactsError(ctx, "You don't have permissions");
                    }
                }
                if (EditMode == null && tripinfoedit == null && emailDto != null) {
                    Intent multi = new Intent(ctx, SelectEmails.class);
                    multi.putExtra(VariableClass.Vari.SELECTEDDATA, emailDto);
                    startActivityForResult(multi, 1001);
                }
            }
        });
        emailAdd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.e("key_press", "" + actionId);
                if (tripinfoedit != null && EditMode != null && EditMode) {
                    if (tripinfoedit.getOwner().equals("1")) {
                        switch (actionId) {
                            case 0:
                                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                                String email = emailAdd.getText().toString().trim();
                                Log.e("clicked", "okay");
                                if (email != null && !email.equals("") && email.matches(emailPattern)) {
                                    // dtomanualAdd = new UserEmail();
                                    mailAddDto = new UserEmail();
                                    mailAddDto.setEmail(email);
                                    mailAddDto.setName(email);
                                    selectedMails.add(mailAddDto);
                                    swipeActionAdapter = new SwipeActionAdapter(new TripEmailsAdapter(selectedMails, ctx));
                                    swipeActionAdapter.setListView(selected_emails);
                                    selected_emails.setAdapter(swipeActionAdapter);
                                    initializeListner();
                                    // selected_emails.setAdapter(new TripEmailsAdapter(selectedMails, ctx));
                                    swipeActionAdapter.notifyDataSetChanged();
                                    emailAdd.setText("");
                                    CommonUtility.showCustomAlertForContactsError(ctx, "Email Added");
                                } else {
                                    if (email != null && !email.equals("") && !email.matches(emailPattern))
                                        CommonUtility.showCustomAlertForContactsError(ctx, "Please Enter Valid Email");
                                }
                                break;
                        }
                    } else
                        CommonUtility.showCustomAlertForContactsError(ctx, "You don't have permissions");
                }
                if (EditMode == null && tripinfoedit == null) {
                    switch (actionId) {
                        case 0:
                            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                            String email = emailAdd.getText().toString().trim();
                            Log.e("clicked", "okay");
                            if (email != null && !email.equals("") && email.matches(emailPattern)) {
                                // dtomanualAdd = new UserEmail();
                                mailAddDto = new UserEmail();
                                mailAddDto.setEmail(email);
                                mailAddDto.setName(email);
                                selectedMails.add(mailAddDto);
                                swipeActionAdapter = new SwipeActionAdapter(new TripEmailsAdapter(selectedMails, ctx));
                                swipeActionAdapter.setListView(selected_emails);
                                selected_emails.setAdapter(swipeActionAdapter);
                                initializeListner();
                                // selected_emails.setAdapter(new TripEmailsAdapter(selectedMails, ctx));
                                swipeActionAdapter.notifyDataSetChanged();
                                emailAdd.setText("");
                                CommonUtility.showCustomAlertForContactsError(ctx, "Email Added");
                            } else {
                                if (email != null && !email.equals("") && !email.matches(emailPattern))
                                    CommonUtility.showCustomAlertForContactsError(ctx, "Please Enter Valid Email");
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    void init() {
        ctx = TripHome.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        Bundle extras = getIntent().getExtras();
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(CommonUtility.getfonttext("Create Trip", TripHome.this));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        error_FontTextView = (TextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        edTripName = (EditText) findViewById(R.id.name_trip);
        emailAdd = (AutoCompleteTextView) findViewById(R.id.enter_email);
        lvEmail = (ListView) findViewById(R.id.email_list);
        selected_emails = (ListView) findViewById(R.id.email_list);
        add_multiEmail = (ImageButton) findViewById(R.id.add_multi_email);
        userService = UserService.getUserServiceInstance(ctx);
        btnGo = (Button) findViewById(R.id.add_mail);
        btnGo.setText("Create trip");
        EmailList = new ArrayList<>();
        selectedMails = new ArrayList<>();
        tripEmailsAdapter = new TripEmailsAdapter(selectedMails, ctx);
        //selected_emails.setAdapter(tripEmailsAdapter);
        swipeActionAdapter = new SwipeActionAdapter(tripEmailsAdapter);
        swipeActionAdapter.setListView(selected_emails);
        selected_emails.setAdapter(swipeActionAdapter);
        if (extras != null) {
            EditMode = getIntent().getExtras().getBoolean("EditMode");
            tripinfoedit = (TripInfo) getIntent().getExtras().getSerializable(VariableClass.Vari.EDITTRIP);
            btnGo.setText("Update Trip");
            edTripName.setText(tripinfoedit.getTripName().toString());
            actionBar.setTitle("Update Trip");
            emailAdd.requestFocus();
        }
        new getEmails().execute();
    }

    class getEmails extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            if (EmailList.size() > 0) {
                searchableAdapter = new SearchableAdapter(ctx, EmailList);
                emailAdd.setAdapter(searchableAdapter);
                tripEmailsAdapter.notifyDataSetChanged();
            }
            CommonUtility.dialog.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(ctx);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final String[] PROJECTION = new String[]{
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Email.DATA
            };
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, null, null, null);
            if (cursor != null) {
                try {
                    final int contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
                    final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    final int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                    long contactId;
                    String displayName, address;
                    while (cursor.moveToNext()) {
                        emailDto = new UserEmail();
                        contactId = cursor.getLong(contactIdIndex);
                        displayName = cursor.getString(displayNameIndex);
                        address = cursor.getString(emailIndex).toLowerCase();
                        emailDto.setId(contactId);
                        emailDto.setEmail(address);
                        emailDto.setName(displayName);
                        EmailList.add(emailDto);
                        emailDto.setAllEmails(EmailList);
                    }
                } finally {
                    cursor.close();
                }
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1001) {
            Log.e("result", "result");
            mailAddDto = (UserEmail) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
            ArrayList<UserEmail> tempTransfer = mailAddDto.getAllEmails();
            Log.e("transfer", "" + tempTransfer.size());
            for (int i = 0; i < tempTransfer.size(); i++) {
                UserEmail tempDto = new UserEmail();
                tempDto.setEmail(tempTransfer.get(i).getEmail());
                tempDto.setName(tempTransfer.get(i).getName());
                selectedMails.add(tempDto);
            }
            swipeActionAdapter = new SwipeActionAdapter(new TripEmailsAdapter(selectedMails, ctx));
            swipeActionAdapter.setListView(selected_emails);
            selected_emails.setAdapter(swipeActionAdapter);
            initializeListner();
            //selected_emails.setAdapter(new TripEmailsAdapter(selectedMails, ctx));
            swipeActionAdapter.notifyDataSetChanged();
            emailAdd.setText("");
            CommonUtility.showCustomAlertForContactsError(ctx, "Email Added");
        }
    }

    void initializeListner() {
        swipeActionAdapter.addBackground(SwipeDirections.DIRECTION_FAR_LEFT, R.layout.message_swipeleft_row_layout)
                .addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT, R.layout.message_swipeleft_row_layout)
                .addBackground(SwipeDirections.DIRECTION_FAR_RIGHT, R.layout.message_swiperight_row_layout)
                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT, R.layout.message_swiperight_row_layout);
        swipeActionAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener() {
            @Override
            public boolean hasActions(int position) {
                // All items can be swiped
                return true;
            }

            @Override
            public boolean shouldDismiss(int position, int direction) {
                // Only dismiss an item when swiping normal left
                return false;
            }

            @Override
            public void onSwipe(int[] positionList, int[] directionList) {
                for (int i = 0; i < positionList.length; i++) {
                    int direction = directionList[i];
                    int position = positionList[i];
                    String dir = "";
                    UserEmail email;
                    email = ((UserEmail) swipeActionAdapter.getItem(position));
                    selectedMails.remove(email);
                    Log.e("sizeList", "" + selectedMails.size());
                }
                Log.e("sizeList", "" + selectedMails.size());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class creatTrip extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            // CommonUtility.dialog.dismiss();
            if (iserr) {
                showErrorMessage(true, response);
            } else {
                tripInfo.setOwner("1");
                userService.addTripInfo(tripInfo);
                if (selectedMails.size() > 0) {
                    shareEmail();
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // showErrorMessage(false, "");
            // CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).createTrip(edTripName.getText().toString());
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
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
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        tripInfo = gson.fromJson(japarent.getJSONObject(0).toString(), TripInfo.class);
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

    class editTrip extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            // CommonUtility.dialog.dismiss();
            CommonUtility.dialog.dismiss();
            if (iserr) {
                showErrorMessage(true, response);
            } else {
                userService.updateTripInfo(tripInfo, tripinfoedit.getTripId());
                CommonUtility.showCustomAlertForContactsError(ctx, "Trip updated");
                if (tripinfoedit.getOwner().equals("1")) {
                    if (selectedMails.size() > 0) {
                        shareEmail();
                    }
                }
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(ctx);
            // showErrorMessage(false, "");
            // CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).editTrip(edTripName.getText().toString(), tripinfoedit.getTripId());
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
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
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        tripInfo = gson.fromJson(japarent.getJSONObject(0).toString(), TripInfo.class);
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

    class shareTrip extends AsyncTask<String, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            if (iserr) {
                // showErrorMessage(true, response);
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
            String mail = params[0];
            // Log.e("emails", mail);
            response = Apis.getApisInstance(ctx).shareTrip(mail, tripInfo.getTripId());
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
//
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

    class tripSahreInfo extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            if (iserr) {
                showErrorMessage(true, response);
            } else {
                if (EditMode != null) {
                    userService.updateTripInfosharelist(trips);
                } else {
                    userService.addtripshareInfo(trips);
                }
                //  CommonUtility.showCustomAlertForContactsError(ctx, "Trip Created Succesfully");
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // showErrorMessage(false, "");
            // CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("tripid", tripInfo.getTripId());
            response = Apis.getApisInstance(ctx).getTripInfo(tripInfo.getTripId());
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
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
                        trips = new ArrayList<>();
                        for (int i = 0; i < japarent.length(); i++) {
                            tripShare = new TripShare();
                            jochild = japarent.getJSONObject(i);
                            tripShare.setEmail(jochild.getString(VariableClass.ResponseVariables.EMAIL));
                            tripShare.setOwner(jochild.getString(VariableClass.ResponseVariables.OWNER));
                            tripShare.setCompanyName(jochild.getString(VariableClass.ResponseVariables.COMPANY_NAME));
                            tripShare.setCompanyId(jochild.getString(VariableClass.ResponseVariables.COMPANY_ID));
                            tripShare.setCompanyType(jochild.getString(VariableClass.ResponseVariables.COMPANY_TYPE));
                            tripShare.setTripId(tripInfo.getTripId());
                            trips.add(tripShare);
                        }
                        //gson.fromJson(japarent.toString(), TripShare.class);
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

    void showErrorMessage(Boolean showm, String message) {
        if (showm) {
            error_FontTextView.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
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

    void shareEmail() {
        ArrayList<String> mails = new ArrayList<>();
//        CommonUtility.show_PDialog(ctx);
        for (index = 0; index < selectedMails.size(); index++) {
            sharedMail = selectedMails.get(index).getEmail();
            if (!mails.contains(sharedMail)) {
                mails.add(sharedMail);
                try {
                    new shareTrip().execute(sharedMail).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        if (EditMode == null)
            CommonUtility.showCustomAlertForContactsError(ctx, "Trip Created Succesfully");
        try {
            new tripSahreInfo().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}








