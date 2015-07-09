package com.Giddh.ui.Activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.Giddh.R;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddEntryByLedger extends AppCompatActivity implements View.OnTouchListener {
    UserService userService;
    Context ctx;
    android.support.v7.app.ActionBar actionBar;
    SummaryAccount summaryEntry;
    EntryInfo entryInfo;
    MaterialEditText eddate, edamount, edvia, edtrip, eddesc;
    private SimpleDateFormat dateFormatter;
    private android.app.DatePickerDialog DatePickerDialog;
    ImageButton clear, save, cancel;
    TripInfo tripinfo;
    Accounts viaacc;
    Boolean debit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_ledger);
        init();
    }

    void init() {
        ctx = AddEntryByLedger.this;
        entryInfo = new EntryInfo();
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        userService = UserService.getUserServiceInstance(ctx);
        summaryEntry = (SummaryAccount) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        if (summaryEntry.getGroupName() != null) {
            if (!summaryEntry.getGroupName().equals("Assets") && !summaryEntry.getGroupName().equals("Liability")) {
                entryInfo.setTransactionType("1");
                entryInfo.setCreditAccount(summaryEntry.getAccountId());
            }
        } else {
            entryInfo.setTransactionType(summaryEntry.getTransactionType());
            entryInfo.setDebitAccount(summaryEntry.getAccountId());
        }
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(CommonUtility.getfonttext("Add Entry", AddEntryByLedger.this));
        edamount = (MaterialEditText) findViewById(R.id.amount);
        eddate = (MaterialEditText) findViewById(R.id.date);
        eddesc = (MaterialEditText) findViewById(R.id.desc);
        edtrip = (MaterialEditText) findViewById(R.id.trip);
        edvia = (MaterialEditText) findViewById(R.id.via);
        clear = (ImageButton) findViewById(R.id.clear);
        save = (ImageButton) findViewById(R.id.save);
        cancel = (ImageButton) findViewById(R.id.cancel);
        edtrip.setInputType(InputType.TYPE_NULL);
        edvia.setInputType(InputType.TYPE_NULL);
        eddate.setInputType(InputType.TYPE_NULL);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        setDateTimeField();
        edvia.setOnTouchListener(this);
        eddate.setOnTouchListener(this);
        edtrip.setOnTouchListener(this);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtrip.setText("");
                clear.setVisibility(View.GONE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edamount.getText().toString() != null && !edamount.getText().toString().equals("")
                        && !edvia.getText().toString().equals("") && edvia.getText().toString() != null) {
                    save.setEnabled(false);
                    entryInfo.setDate(eddate.getText().toString());
                    entryInfo.setGroupId(summaryEntry.getGroupName());
                    entryInfo.setCompanyId(Prefs.getCompanyId(ctx));
                    entryInfo.setEmail(Prefs.getEmailId(ctx));
                    entryInfo.setAmount(Double.parseDouble(edamount.getText().toString()));
                    entryInfo.setDescription(eddesc.getText().toString());
                    if (entryInfo.getTripId() != null && !entryInfo.getTripId().equals("")) {
                        userService.addTripentrydata(entryInfo);
                    }
                    userService.addentrydata(entryInfo);

                    if (CommonUtility.isNetworkAvailable(ctx))
                        CommonUtility.syncwithServer(ctx);
                    Intent i = new Intent(ctx, SummaryInfo.class);
                    startActivity(i);
                } else {
                    CommonUtility.showCustomAlertForContactsError(ctx, "Some fields are missing");
                }
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

                case R.id.via:
                    if (debit)
                        entryInfo.setDebitAccount(null);
                    else entryInfo.setCreditAccount(null);
                    if (summaryEntry.getGroupName() != null) {
                        if (summaryEntry.getGroupName().equals("Assets") || summaryEntry.getGroupName().equals("Liability")) {
                            if (summaryEntry.getTransactionType().equals("0")) {
                                Intent via = new Intent(ctx, SelectInfo.class);
                                via.putExtra(VariableClass.Vari.SELECTEDDATA, 5);

                                if (summaryEntry.getGroupName().equals("Assets") && !summaryEntry.getAccountName().equalsIgnoreCase("Cash")) {
                                    via.putExtra("CashInAtm", true);
                                } else {
                                    via.putExtra("CashInAtm", false);
                                }
                                startActivityForResult(via, 102);
                            } else {
                                Intent via = new Intent(ctx, SelectInfo.class);
                                via.putExtra(VariableClass.Vari.SELECTEDDATA, 6);
                                if (summaryEntry.getGroupName().equals("Assets") && !summaryEntry.getAccountName().equalsIgnoreCase("Cash")) {
                                    via.putExtra("CashInAtm", true);
                                } else {
                                    via.putExtra("CashInAtm", false);

                                }
                                startActivityForResult(via, 102);
                            }
                        }
                    } else {
                        Intent via = new Intent(ctx, SelectInfo.class);
                        via.putExtra(VariableClass.Vari.SELECTEDDATA, 3);
                        via.putExtra("CashInAtm", false);
                        startActivityForResult(via, 102);
                    }
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
                case 102:
                    viaacc = (Accounts) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
                    if (viaacc != null) {
                        edvia.setText(viaacc.getAccountName());
                        if (entryInfo.getDebitAccount() != null) {
                            entryInfo.setCreditAccount(viaacc.getAcc_webId());
                            debit = false;
                        } else {
                            entryInfo.setDebitAccount(viaacc.getAcc_webId());
                            debit = true;
                        }
                    }
                    edvia.requestFocus();
                    break;
                case 103:
                    tripinfo = (TripInfo) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
                    if (tripinfo != null) {
                        edtrip.setText(tripinfo.getTripName());
                        clear.setVisibility(View.VISIBLE);
                        edtrip.requestFocus();
                        entryInfo.setTripId(tripinfo.getTripId());
                    }
                    break;
            }
        }
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        eddate.setText(dateFormatter.format(newCalendar.getTime()));
        DatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                eddate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

}
