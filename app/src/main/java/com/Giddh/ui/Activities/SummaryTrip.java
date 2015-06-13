package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.SummaryDetailsAdapter;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.SummaryAccount;
import com.Giddh.dtos.SummaryEntry;
import com.Giddh.dtos.TripShare;
import com.Giddh.util.UserService;
import com.splunk.mint.Mint;

import java.util.ArrayList;

public class SummaryTrip extends AppCompatActivity {
    ActionBar actionBar;
    Context ctx;
    TripShare tripShare;
    ExpandableListView listExpandable;
    UserService userService;
    ArrayList<SummaryEntry> entries;
    TextView emp_view;
    SummaryDetailsAdapter summaryDetailsAdapter;
    SummaryAccount summaryAccount;
    View addnewTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summarydetails);
        ctx = SummaryTrip.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        userService = UserService.getUserServiceInstance(ctx);
        tripShare = (TripShare) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        summaryAccount = new SummaryAccount();
        listExpandable = (ExpandableListView) findViewById(R.id.details_list);
        emp_view = (TextView) findViewById(R.id.emp_view);
        addnewTransaction=findViewById(R.id.add_transaction);
        listExpandable.setEmptyView(emp_view);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
     //   actionBar.setIcon(R.drawable.small_summary);
        entries = new ArrayList<>();
        actionBar.setTitle(CommonUtility.getfonttext(tripShare.getEmail(), SummaryTrip.this));
        // getSupportActionBar().setTitle(" " + (Html.fromHtml("<font color=\"#FFFFFF\">" + tripShare.getEmail() + "</font>")));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        entries = userService.getTripInfo(tripShare.getTripId(), tripShare.getCompanyId());
        summaryDetailsAdapter = new SummaryDetailsAdapter(ctx, entries, true);
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
}
