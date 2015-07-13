package com.Giddh.ui.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.Giddh.R;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.SummaryAccount;
import com.Giddh.dtos.TripInfo;
import com.Giddh.util.UserService;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class EditEntry extends AppCompatActivity implements View.OnTouchListener {
    UserService userService;
    Context ctx;
    android.support.v7.app.ActionBar actionBar;
    SummaryAccount summaryEntry;
    ArrayList<EntryInfo> entry;
    EntryInfo entryInfo, entryInfoupdate;
    MaterialEditText eddate, edamount, edfor, edvia, edtrip, eddesc;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog DatePickerDialog;
    ImageButton clear, save, cancel;
    TripInfo tripinfo;
    Accounts debcre, viaacc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_entry);
        init();
    }

    void init() {
        ctx = EditEntry.this;
        userService = UserService.getUserServiceInstance(ctx);
        summaryEntry = (SummaryAccount) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        summaryEntry.getEyId();
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        entryInfoupdate = new EntryInfo();
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(CommonUtility.getfonttext("Edit Entry", EditEntry.this));
        edamount = (MaterialEditText) findViewById(R.id.amount);
        eddate = (MaterialEditText) findViewById(R.id.date);
        eddesc = (MaterialEditText) findViewById(R.id.desc);
        edfor = (MaterialEditText) findViewById(R.id.for_val);
        edtrip = (MaterialEditText) findViewById(R.id.trip);
        edvia = (MaterialEditText) findViewById(R.id.via);
        clear = (ImageButton) findViewById(R.id.clear);
        save = (ImageButton) findViewById(R.id.save);
        cancel = (ImageButton) findViewById(R.id.cancel);
        edfor.setInputType(InputType.TYPE_NULL);
        edtrip.setInputType(InputType.TYPE_NULL);
        edvia.setInputType(InputType.TYPE_NULL);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        entry = new ArrayList<>();
        entry = userService.getallnullEntryId(false, summaryEntry.getEyId());
        entryInfo = entry.get(0);
        entryInfoupdate.setTransactionType(entryInfo.getTransactionType());
        entryInfoupdate.setCompanyId(entryInfo.getCompanyId());
        if (entryInfo.getTransactionType().equals("1")) {
            edfor.setText(userService.getaccountnameorId(entryInfo.getDebitAccount()).getAccountName());
            edvia.setText(userService.getaccountnameorId(entryInfo.getCreditAccount()).getAccountName());
        } else {
            edfor.setText(userService.getaccountnameorId(entryInfo.getCreditAccount()).getAccountName());
            edvia.setText(userService.getaccountnameorId(entryInfo.getDebitAccount()).getAccountName());
        }
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        eddate.setText(entryInfo.getDate());
        edamount.setText(String.valueOf(entryInfo.getAmount()));
        if (entryInfo.getDescription() != null) {
            eddesc.setText(entryInfo.getDescription());
        }
        if (entryInfo.getTripId() != null && !entryInfo.getTripId().equals("") && !entryInfo.getTripId().equals("0")) {
            edtrip.setText(userService.getallTripInfo(entryInfo.getTripId(), false).get(0).getTripName());
            clear.setVisibility(View.VISIBLE);
            entryInfoupdate.setTripId(entryInfo.getTripId());
        }
        setDateTimeField();
        edvia.setOnTouchListener(this);
        eddate.setOnTouchListener(this);
        edfor.setOnTouchListener(this);
        edtrip.setOnTouchListener(this);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtrip.setText("");
                clear.setVisibility(View.GONE);
                entryInfoupdate.setTripId(null);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryInfoupdate.setDate(eddate.getText().toString());
                if (edamount.getText().toString() != null && !edamount.getText().toString().equals("")) {
                    entryInfoupdate.setAmount(Double.valueOf(edamount.getText().toString()));
                }
                if (edamount.getText().toString() != null && edamount.getText().toString().length() < 31) {
                    entryInfoupdate.setDescription(eddesc.getText().toString());
                }
                if (entryInfo.getTransactionType().equals("0")) {
                    if (viaacc != null) {
                        entryInfoupdate.setDebitAccount(viaacc.getAcc_webId());
                    } else {
                        entryInfoupdate.setDebitAccount(userService.
                                getaccountnameorId(edvia.getText().toString()).getAcc_webId());
                    }
                    if (debcre != null)
                        entryInfoupdate.setCreditAccount(debcre.getAcc_webId());
                    else
                        entryInfoupdate.setCreditAccount(userService.
                                getaccountnameorId(edfor.getText().toString()).getAcc_webId());
                } else {
                    if (viaacc != null) {
                        entryInfoupdate.setCreditAccount(viaacc.getAcc_webId());
                    } else {
                        entryInfoupdate.setCreditAccount(userService.
                                getaccountnameorId(edvia.getText().toString()).getAcc_webId());
                    }
                    if (debcre != null)
                        entryInfoupdate.setDebitAccount(debcre.getAcc_webId());
                    else
                        entryInfoupdate.setDebitAccount(userService.
                                getaccountnameorId(edfor.getText().toString()).getAcc_webId());
                }

               /* if (tripinfo != null) {
                    entryInfoupdate.setTripId(tripinfo.getTripId());
                    clear.setVisibility(View.VISIBLE);
                } else if (!edtrip.getText().toString().equals("")) {
                    entryInfoupdate.setTripId(entryInfo.getTripId());
                    clear.setVisibility(View.VISIBLE);
                }*/

                if (eddesc.getText().toString() != null && !eddesc.getText().toString().equals("")) {
                    entryInfoupdate.setDescription(eddesc.getText().toString());
                }
                try {
                    new EditEntrySerever().execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                userService.update_entry(entryInfoupdate, summaryEntry.getEyId());
                entryInfoupdate.setEmail(Prefs.getEmailId(ctx));
                if (edtrip.getText().toString() != null && !edtrip.getText().toString().equals("")) {
                    if (entryInfoupdate.getTripId() != null && !entryInfoupdate.getTripId().equals("")) {
                        if (entryInfo.getTripId() != null && !entryInfo.getTripId().equals("")) {
                            userService.update_entry_trip(entryInfoupdate);
                            userService.deleteEntry(Integer.parseInt(entryInfo.getTripId()), true, entryInfo.getEntryId());

                        } else {

                            userService.addTripentrydata(entryInfoupdate);
                        }
                    }
                } else if (!entryInfo.getTripId().equals("null") && entryInfo.getTripId() != null) {
                    userService.deleteEntry(Integer.parseInt(entryInfo.getTripId()), true, entryInfo.getEntryId());
                }
             /*   if (!edtrip.getText().toString().equals("") && entryInfoupdate.getTripId() != null && !entryInfoupdate.getTripId().equals("")) {
                    userService.update_entry_trip(entryInfoupdate);
                } else if (!edtrip.getText().toString().equals("") && entryInfo.getTripId() != null && !entryInfo.getTripId().equals("")) {
                    userService.addTripentrydata(entryInfoupdate);
                }*/
                CommonUtility.showCustomAlertForContactsError(ctx, "Succesfully updated");
                Intent detail = new Intent(ctx, SummaryInfo.class);
              /*  summaryEntry.setAccountName(userService.getaccountnameorId
                        (summaryEntry.getAccountId()).getAccountName());*/
                //  detail.putExtra(VariableClass.Vari.SELECTEDDATA, summaryEntry);
                ctx.startActivity(detail);
                detail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                EditEntry.this.finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.date:
                    DatePickerDialog.show();
                    break;
                case R.id.trip:
                    Intent trip = new Intent(ctx, SelectInfo.class);
                    trip.putExtra(VariableClass.Vari.SELECTEDDATA, 2);
                    trip.putExtra("CashInAtm", false);
                    startActivityForResult(trip, 103);
                    break;
                case R.id.for_val:
                    Intent rec = new Intent(ctx, SelectInfo.class);
                    rec.putExtra("CashInAtm", false);
                    if (entryInfo.getTransactionType().equals("0")) {
                        rec.putExtra(VariableClass.Vari.SELECTEDDATA, 0);
                    } else {
                        rec.putExtra(VariableClass.Vari.SELECTEDDATA, 1);
                    }
                    startActivityForResult(rec, 101);
                    break;
                case R.id.via:
                    Intent via = new Intent(ctx, SelectInfo.class);
                    via.putExtra(VariableClass.Vari.SELECTEDDATA, 3);
                    via.putExtra("CashInAtm", false);
                    startActivityForResult(via, 102);
                    break;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 101:
                    debcre = (Accounts) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
                    if (debcre != null) {
                        edfor.setText(debcre.getAccountName());
                        if (debcre.getGroupId().equals("3")) {
                            edvia.setText("Cash");
                            viaacc = userService.getaccountnameorId("Cash");
                            edvia.setEnabled(false);
                        } else edvia.setEnabled(true);
                    }
                    edfor.requestFocus();
                    break;
                case 102:
                    viaacc = (Accounts) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
                    if (viaacc != null)
                        edvia.setText(viaacc.getAccountName());
                    edvia.requestFocus();
                    break;
                case 103:
                    tripinfo = (TripInfo) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
                    if (tripinfo != null) {
                        entryInfoupdate.setTripId(tripinfo.getTripId());
                        edtrip.setText(tripinfo.getTripName());
                        clear.setVisibility(View.VISIBLE);
                        edtrip.requestFocus();
                    }
                    break;
            }
        }
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                eddate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    class EditEntrySerever extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            CommonUtility.dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonUtility.show_PDialog(ctx);
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).editentry(
                    CommonUtility.getJsonArray(String.valueOf(entryInfoupdate.getAmount()),
                            entryInfoupdate.getDebitAccount(), ctx, entryInfoupdate.getGroupId()),
                    CommonUtility.getJsonArray(String.valueOf(entryInfoupdate.getAmount()),
                            entryInfoupdate.getCreditAccount(), ctx, entryInfoupdate.getGroupId()),
                    entryInfoupdate.getTripId(), entryInfoupdate.getDescription(), entryInfoupdate.getDate(), entryInfo.getEntryId());
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
                        entryInfoupdate.setEntryId(japarent.getJSONObject(0).getString(VariableClass.ResponseVariables.ENTRY_ID));
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
