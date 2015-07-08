package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.CompanyListAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.AccountDetails;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.Company;
import com.Giddh.dtos.CompanyDetails;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.GroupDetails;
import com.Giddh.dtos.GroupInfo;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    ListView companyList;
    ArrayList<Company> companies = null;
    CompanyListAdapter adapter;
    Context ctx;
    RelativeLayout error_layout;
    TextView error_FontTextView;
    Company selecteddto;
    ActionBar actionBar;
    UserService userService;
    Accounts acdto;
    GroupInfo gdto;
    ArrayList<String> sms;
    EntryInfo entryInfo;
    Accounts bankacc, wallet;
    private static long back_pressed;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ctx = HomeActivity.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        bankacc = new Accounts();
        wallet = new Accounts();
        userService = UserService.getUserServiceInstance(ctx);
        companyList = (ListView) findViewById(R.id.company_list);
        error_FontTextView = (TextView) findViewById(R.id.error_text);
        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.resize_white_logo);
        //   actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>" + CommonUtility.getfonttext(" Giddh", HomeActivity.this) + "</font>"));
        actionBar.setTitle(CommonUtility.getfonttext(" Giddh", HomeActivity.this));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        companies = new ArrayList<>();
        if (CommonUtility.isNetworkAvailable(ctx)) {
            new getCompanyNames().execute();
        } else {
            companies = userService.getcompaniesList(Prefs.getEmailId(ctx));
            adapter = new CompanyListAdapter(companies, ctx);
            companyList.setAdapter(adapter);
        }
        companyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Company company = (Company) parent.getItemAtPosition(position);
                Prefs.setCompanyId(ctx, company.getCompanyId());
                Prefs.setCompanyName(ctx, company.getCompanyName());
                if (company.getCompanyType().equals("1")) {
                    if (userService.getSumExpenceIncomeEntry("transactionType", "0") != 0 ||
                            userService.getSumExpenceIncomeEntry("transactionType", "1") != 0) {
                        Intent intent = new Intent(ctx, SummaryInfo.class);
                        startActivity(intent);
                        HomeActivity.this.finish();
                    } else {
                        if (VariableClass.Vari.LOGIN_FIRST_TIME) {
                            int bank = matdialog("This amount will be use as your opening balance "
                                    , "Enter amount in you Bank account", true, false);
                            VariableClass.Vari.LOGIN_FIRST_TIME = false;
                            VariableClass.Vari.MESSEGE_FIRST_TIME = false;
                        } else {
                            Intent intent = new Intent(ctx, AskType.class);
                            company = (Company) parent.getItemAtPosition(position);
                            intent.putExtra(VariableClass.Vari.SELECTEDDATA, company);
                            startActivity(intent);
                        }
                    }
                } else {
                    Intent intent = new Intent(ctx, TrailBalancePage.class);
                    company = (Company) parent.getItemAtPosition(position);
                    intent.putExtra(VariableClass.Vari.SELECTEDDATA, company);
                    startActivity(intent);
                }
               /* if (company.getCompanyType().equals("1") && VariableClass.Vari.LOGIN_FIRST_TIME) {
                    int bank = matdialog("This amount will be use as your opening balance ", "Enter amount in you Bank account", true, false);
                    VariableClass.Vari.LOGIN_FIRST_TIME = false;
                    VariableClass.Vari.MESSEGE_FIRST_TIME = false;
                } else {
                    if (company.getCompanyType().equals("1") && VariableClass.Vari.LOGIN_FIRST_TIME) {
                        Intent intent = new Intent(ctx, AskType.class);
                        company = (Company) parent.getItemAtPosition(position);
                        intent.putExtra(VariableClass.Vari.SELECTEDDATA, company);
                        startActivity(intent);
                    } else {
                        if (company.getCompanyType().equals("1")) {
                            Intent intent = new Intent(ctx, SummaryInfo.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ctx, TrailBalancePage.class);
                            company = (Company) parent.getItemAtPosition(position);
                            intent.putExtra(VariableClass.Vari.SELECTEDDATA, company);
                            startActivity(intent);
                        }
                    }
                }*/
            }
        });
        if (CommonUtility.isNetworkAvailable(ctx))
            new DelEntries().execute();

    }

    class getCompanyNames extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {


            companies = userService.getcompaniesList(Prefs.getEmailId(ctx));
            Prefs.setCompanysize(ctx, String.valueOf(companies.size()));
            if (iserr) {
                adapter = new CompanyListAdapter(companies, ctx);
                companyList.setAdapter(adapter);
                adapter.notifyDataSetInvalidated();
                adapter.notifyDataSetChanged();
            } else {
                if (companies.size() > 0) {
                    companyList.setVisibility(View.VISIBLE);
                    adapter = new CompanyListAdapter(companies, ctx);
                    companyList.setAdapter(adapter);
                    adapter.notifyDataSetInvalidated();
                    adapter.notifyDataSetChanged();
                } else {
                    companyList.setVisibility(View.GONE);
                }
            }
            if (CommonUtility.isNetworkAvailable(ctx)) {
                new AccountList().execute();
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            showErrorMessage(false, "");

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).getCompanyNames();
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                JSONArray jachild = null;
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
                        for (int i = 0; i < japarent.length(); i++) {
                            selecteddto = new Company();
                            jochild = japarent.getJSONObject(i);
                            selecteddto.setCompanyName(jochild.getString(VariableClass.ResponseVariables.COMPANY_NAME));
                            selecteddto.setCompanyId(jochild.getString(VariableClass.ResponseVariables.COMPANY_ID));
                            selecteddto.setCompanyType(jochild.getString(VariableClass.ResponseVariables.COMPANY_TYPE));
                            selecteddto.setFinancialYear(jochild.getString(VariableClass.ResponseVariables.FINANCIAL_YEAR));
                            selecteddto.setEmailId(Prefs.getEmailId(ctx));
                            if (selecteddto.getCompanyType().equals("1")) {
                                Prefs.setUserName(ctx, selecteddto.getCompanyName());
                                Prefs.setCompanyName(ctx, selecteddto.getCompanyName());
                                Prefs.setCompanyId(ctx, selecteddto.getCompanyId());
                            }
                            companies.add(selecteddto);
                        }
                        userService.addCompanyList(companies);
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

            if (VariableClass.Vari.FIRST_TIME_ENTRY) {
                String[] accNames = getResources().getStringArray(R.array.accountName);
                int[] group_ids = {1, 1, 1, 1, 1, 1, 1, 0, 5, 1, 3, 1, 0, 1, 0, 1, 0};
                String[] group_parentId = {"0", "1", "2", "3"};
                String[] group_name = {"Income", "Expense", "Liability", "Assets"};
                Log.e("number_of_entries", String.valueOf(userService.getallAccounts("1", false, false).size()));
                for (int i = 0; i < accNames.length; i++) {
                    acdto = new Accounts();
                    acdto.setOpeningBalance(0);
                    acdto.setAcc_webId(String.valueOf(i + 1));
                    if (accNames[i].equals("Rent")) {
                        if (group_ids[i] == 1) {
                            acdto.setUniqueName("ExpenseRent");
                        } else {
                            acdto.setUniqueName("IncomeRent");
                        }
                    }
                    if (accNames[i].equals("Other")) {
                        if (group_ids[i] == 1) {
                            acdto.setUniqueName("ExpenseOther");
                        } else {
                            acdto.setUniqueName("IncomeOther");
                        }
                    }
                    Locale current = getResources().getConfiguration().locale;
                    Currency currency = Currency.getInstance(current);
                    //  Prefs.setCurrency(ctx, currency.getSymbol());
                    Prefs.setCurrency(ctx, "");
                    Prefs.setCountry(ctx, ctx.getResources().getConfiguration().locale.getDisplayCountry());
                    acdto.setAccountName(accNames[i]);
                    Log.e("webid", acdto.getAcc_webId());
                    acdto.setGroupId(String.valueOf(group_ids[i]));
                    userService.addaccountsdata(acdto);
                }
                for (int j = 0; j < group_name.length; j++) {
                    gdto = new GroupInfo();
                    gdto.setGroupName(group_name[j]);
                    gdto.setParentId(group_parentId[j]);
                    gdto.setSystemId(group_parentId[j]);
                    userService.addgroupdata(gdto);
                }
                VariableClass.Vari.FIRST_TIME_ENTRY = false;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                CommonUtility.clearData(ctx, this);
                VariableClass.Vari.LOGIN_FIRST_TIME = true;
                VariableClass.Vari.MESSEGE_FIRST_TIME = true;
                Intent SignUp = new Intent(ctx, SplashScreen.class);
                SignUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SignUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(SignUp);
                this.finish();
                return true;
            case R.id.settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_screem, menu);
        return super.onCreateOptionsMenu(menu);
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
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else {
            new AlertDialogWrapper.Builder(ctx)
                    .setTitle("Are you sure you want to exit?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HomeActivity.this.finish();
                }
            }).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    public int matdialog(String Content, String tittle, final Boolean isbank, final Boolean isbankname) {
        final int[] res = new int[1];
        new MaterialDialog.Builder(ctx)
                .title(tittle)
                .content(Content).positiveText("OK").negativeText("CANCEL")
                .input("2000 $", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!input.toString().equals("") && input.toString() != null) {
                            if (isbank && !isbankname) {
                                int id = userService.getmaxaccId() + 1;
                                bankacc.setAcc_webId(String.valueOf(id));
                                bankacc.setGroupId("3");
                                bankacc.setOpeningBalance(Double.parseDouble(input.toString()));
                                int bankname = matdialog("", "Enter Bank name ", true, true);
                            }
                            if (isbank && isbankname) {
                                bankacc.setAccountName(input.toString());
                                userService.addaccountsdata(bankacc);
                                int wallet = matdialog("This amount will be use as your Opening balance ", "Enter amount in you wallet", false, false);
                            }
                            if (!isbank && !isbankname) {
                                wallet.setAccountName("Cash");
                                wallet.setOpeningBalance(Double.parseDouble(input.toString()));
                                userService.addaccountsdata(wallet);
                            }
                        } else {
                            CommonUtility.showCustomAlertForContactsError(ctx, "no value added");
                        }
                    }
                }).show();
        return res[0];
    }

    class AccountList extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {

            CommonUtility.syncwithServer(ctx);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // showErrorMessage(false, "");

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).getAccountList();
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
                                AccountDetails accountDetails;
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
            response = Apis.getApisInstance(ctx).getalldelete();
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


}