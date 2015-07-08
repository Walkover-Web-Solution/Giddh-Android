package com.Giddh.ui.Activities;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.TrialBalanceAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Company;
import com.Giddh.dtos.CompanyDetails;
import com.Giddh.dtos.GroupDetails;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TrailBalancePage extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;
    Company companydto;
    ListView lnGroplist;
    ArrayList<GroupDetails> mainGroupNames;
    RelativeLayout error_layout;
    TextView error_FontTextView;
    EditText edTodate;
    CompanyDetails companyDetailsdto, companyDtoSearch;
    TrialBalanceAdapter adapter;
    GroupDetails groupDetails;
    private DatePickerDialog toDatePickerDialog;
    Context ctx;
    private boolean mIsSearchResultView = false;
    private SimpleDateFormat dateFormatter;
    String searchvalue, currentDateandTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trialbalance_page);
        ctx = TrailBalancePage.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        companydto = (Company) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(" " + CommonUtility.getfonttext(companydto.getCompanyName(), TrailBalancePage.this));
        actionBar.setDisplayShowTitleEnabled(false);
        Bitmap image = CommonUtility.drawImage(companydto.getCompanyName(), TrailBalancePage.this);
        BitmapDrawable icon = new BitmapDrawable(getResources(), image);
        actionBar.setIcon(icon);
        lnGroplist = (ListView) findViewById(R.id.list_group);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        error_FontTextView = (TextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        edTodate = (EditText) findViewById(R.id.etxt_todate);
        edTodate.setInputType(InputType.TYPE_NULL);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        currentDateandTime = dateFormatter.format(cal.getTime());
        edTodate.setText(dateFormatter.format(new java.util.Date()));
        if (CommonUtility.isNetworkAvailable(ctx))
            new getTrialBalance().execute();
        setDateTimeField();
        edTodate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!edTodate.getText().toString().equalsIgnoreCase("")) {
                    new getTrialBalance().execute();
                }
            }
        });
        lnGroplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj1 = parent.getItemAtPosition(position);
                Log.e("lnGroup", "lnGroup");
                groupDetails = mainGroupNames.get(position);
                if (groupDetails.getSubGroupDetails() != null)
                    if (groupDetails.getSubGroupDetails().size() > 0) {
                        Intent intent = new Intent(ctx, SubGroupPage.class);
                        intent.putExtra(VariableClass.Vari.SELECTEDDATA, groupDetails);
                        startActivity(intent);
                    }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trialbal, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        menu.setGroupVisible(R.id.main_menu_group, true);
        if (mIsSearchResultView) {
            searchItem.setVisible(false);
        }
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                if (queryText.length() >= 4) {
                    searchvalue = queryText;
                    new getSearchResults().execute();
                } else
                    CommonUtility.showCustomAlertForContactsError(ctx, "Please enter greater than four characters");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("on query text change ", "on query text change");
                return true;
            }
        });
        return true;
    }

    private void setDateTimeField() {
        edTodate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    java.util.Date fin_yr = dateFormatter.parse(companydto.getFinancialYear());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fin_yr);
                    toDatePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                    cal.add(Calendar.YEAR, -1);
                    toDatePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                toDatePickerDialog.show();
                return false;
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edTodate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private class getTrialBalance extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;
        MaterialDialog dialog;

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            dialog.cancel();
            super.onPostExecute(result);
            if (mainGroupNames != null)
                if (mainGroupNames.size() > 0) {
                    lnGroplist.setVisibility(View.VISIBLE);
                    adapter = new TrialBalanceAdapter(mainGroupNames, ctx, false);
                    lnGroplist.setAdapter(adapter);
                    adapter.notifyDataSetInvalidated();
                    adapter.notifyDataSetChanged();
                } else {
                    lnGroplist.setVisibility(View.GONE);
                }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new MaterialDialog.Builder(ctx)
                    .title("Loading")
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).getTrialBalance(companydto.getCompanyName(), currentDateandTime, edTodate.getText().toString());
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
                        mainGroupNames = new ArrayList<>();
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        companyDetailsdto = gson.fromJson(japarent.getJSONObject(0).toString(), CompanyDetails.class);
                        mainGroupNames = companyDetailsdto.getGroupDetail();
                        companyDetailsdto.getGroupDetail();
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

    class getSearchResults extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            CommonUtility.dialog.dismiss();
            if (mainGroupNames.size() > 0) {
                lnGroplist.setVisibility(View.VISIBLE);
                adapter = new TrialBalanceAdapter(mainGroupNames, ctx, false);
                lnGroplist.setAdapter(adapter);
                adapter.notifyDataSetInvalidated();
                adapter.notifyDataSetChanged();
            } else {
                lnGroplist.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(ctx);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).getSearchResult(companydto.getCompanyName(), searchvalue, edTodate.getText().toString(), currentDateandTime);
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
                        mainGroupNames = new ArrayList<>();
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        companyDtoSearch = gson.fromJson(japarent.getJSONObject(0).toString(), CompanyDetails.class);
                        mainGroupNames = companyDtoSearch.getGroupDetail();
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

