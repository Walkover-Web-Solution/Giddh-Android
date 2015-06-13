package com.Giddh.ui.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.SummaryDetailsAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.SummaryAccount;
import com.Giddh.dtos.SummaryEntry;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SummaryDetails extends AppCompatActivity {
    ActionBar actionBar;
    Context ctx;
    SummaryAccount summaryAccount;
    ExpandableListView listExpandable;
    UserService userService;
    TextView emp_view;
    ArrayList<SummaryEntry> entries;
    SummaryDetailsAdapter summaryDetailsAdapter;
    View addnewTransaction;
    TextView opening, closing;
    Activity[] lastfour_screen = new Activity[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summarydetails);
        ctx = SummaryDetails.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        userService = UserService.getUserServiceInstance(ctx);
        summaryAccount = (SummaryAccount) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        if (CommonUtility.summary_stack.size() == 0) {
            CommonUtility.firstTIme = true;
        }
        listExpandable = (ExpandableListView) findViewById(R.id.details_list);
        emp_view = (TextView) findViewById(R.id.emp_view);
        addnewTransaction = findViewById(R.id.add_transaction);
        listExpandable.setEmptyView(emp_view);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_summary_detail, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        final ImageButton addtrans = (ImageButton) mCustomView
                .findViewById(R.id.addtransactions);
        opening = (TextView) mCustomView.findViewById(R.id.op_balance);
        closing = (TextView) mCustomView.findViewById(R.id.cl_balance);
        mTitleTextView.setText(CommonUtility.getfonttext(summaryAccount.getAccountName(), SummaryDetails.this));
        String openingb = userService.getopening_balance(summaryAccount.getAccountName());
        Double val1 = userService.getclosing_bal(summaryAccount.getAccountId(), false);
        Double val2 = userService.getclosing_bal(summaryAccount.getAccountId(), true);
        Double closingb = Double.valueOf(openingb) - (val1 - val2);
        opening.setText("Opening =" + openingb);
        closing.setText("Closing  =" + closingb);
        //imageButton.setBackgroundResource(R.drawable.small_summary);
        addtrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trans = new Intent(ctx, AskType.class);
                startActivity(trans);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        entries = new ArrayList<>();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        if (summaryAccount.getGroup() != null) {
            entries = userService.getEntryInfoIncomeExpense(summaryAccount.getTransactionType());
        } else
            entries = userService.getEntryInfo(summaryAccount.getAccountId());
        summaryDetailsAdapter = new SummaryDetailsAdapter(ctx, entries, false);
        listExpandable.setAdapter(summaryDetailsAdapter);
        for (int i = 0; i < entries.size(); i++) {
            listExpandable.expandGroup(i);
        }
        addnewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent multi = new Intent(ctx, AskType.class);
                startActivity(multi);
            }
        });
        listExpandable.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    new MaterialDialog.Builder(ctx)
                            .items(R.array.edit_trans)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    switch (which) {
                                        case 0:
                                            new AlertDialogWrapper.Builder(ctx)
                                                    .setTitle("delete this entry?")
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    SummaryAccount saccount = (SummaryAccount) parent.getAdapter().getItem(position);
                                                    if (!CommonUtility.isNetworkAvailable(ctx)) {
                                                        SummaryAccount sid = (SummaryAccount) parent.getItemAtPosition(position);
                                                        userService.adddeletedEntryid(sid.getEntryId());
                                                    }
                                                    new DeleteEntryFromServer().execute(saccount.getEntryId());
                                                    userService.deleteEntry(saccount.getEyId(), false, null);
                                                    if (saccount.getTripid() != null && !saccount.getTripid().equals("")
                                                            && !saccount.getTripid().equals("0")) {
                                                        userService.deleteEntry(Integer.parseInt(saccount.getTripid()), true, saccount.getEntryId());
                                                    }
                                                    summaryDetailsAdapter.notifyDataSetChanged();
                                                    entries = userService.getEntryInfo(summaryAccount.getAccountId());
                                                    summaryDetailsAdapter = new SummaryDetailsAdapter(ctx, entries, false);
                                                    listExpandable.setAdapter(summaryDetailsAdapter);
                                                    for (int i = 0; i < entries.size(); i++) {
                                                        listExpandable.expandGroup(i);
                                                    }
                                                }
                                            }).show();
                                            break;
                                        case 1:
                                            Intent i = new Intent(ctx, EditEntry.class);
                                            i.putExtra(VariableClass.Vari.SELECTEDDATA, (SummaryAccount) parent.getAdapter().getItem(position));
                                            startActivity(i);
                                            break;
                                    }
                                }
                            })
                            .show();
                }
                return true;
            }
        });
        listExpandable.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                SummaryAccount summaryAcc;
                summaryAcc = (SummaryAccount) summaryDetailsAdapter.getChild(groupPosition, childPosition);
                Intent detail = new Intent(ctx, SummaryDetails.class);
                summaryAcc.setAccountName(userService.getaccountnameorId(summaryAcc.getAccountId()).getAccountName());
                detail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                detail.putExtra(VariableClass.Vari.SELECTEDDATA, summaryAcc);
                ctx.startActivity(detail);
                CommonUtility.firstTIme = false;
                if (CommonUtility.summary_stack.size() == 4) {
                    CommonUtility.summary_stack.remove(0);
                    CommonUtility.summary_stack.add(summaryAccount);
                } else {
                    CommonUtility.summary_stack.add(summaryAccount);
                }
                SummaryDetails.this.finish();
                return true;
            }
        });
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            listExpandable.setIndicatorBounds(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
        } else {
            listExpandable.setIndicatorBoundsRelative(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
        }
    }

    class DeleteEntryFromServer extends AsyncTask<String, Void, Void> {
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
            response = Apis.getApisInstance(ctx).deleteEntry(params[0]);
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

    @Override
    public void onBackPressed() {
        if (CommonUtility.firstTIme) {
            Intent intent = new Intent(getBaseContext(), SummaryInfo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            SummaryDetails.this.finish();
        } else {
            Intent intent = new Intent(getBaseContext(), SummaryDetails.class);
            intent.putExtra(VariableClass.Vari.SELECTEDDATA, CommonUtility.summary_stack.get(CommonUtility.summary_stack.size() - 1));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            CommonUtility.summary_stack.get(CommonUtility.summary_stack.size() - 1)
                    .setAccountName(userService.getaccountnameorId(CommonUtility.summary_stack.get(CommonUtility.summary_stack.size() - 1).getAccountId()).getAccountName());
            CommonUtility.summary_stack.remove(CommonUtility.summary_stack.size() - 1);
            startActivity(intent);
            SummaryDetails.this.finish();
        }
    }
}
