package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.Giddh.R;
import com.Giddh.adapters.AccountDetailsAdapter;
import com.Giddh.adapters.TrialBalanceAdapter;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.NonScrollListView;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.AccountDetails;
import com.Giddh.dtos.GroupDetails;
import com.splunk.mint.Mint;

import java.util.ArrayList;

public class SubGroupPage extends AppCompatActivity {
    ActionBar actionBar;
    NonScrollListView lvSubGroplist, lvAccountDetails;
    ArrayList<GroupDetails> subgroups;
    ArrayList<AccountDetails> accountsList = null;
    TrialBalanceAdapter adapter;
    AccountDetailsAdapter accountDetailsAdapter;
    GroupDetails groupDetails;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_group);
        ctx = SubGroupPage.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        groupDetails = (GroupDetails) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle(" " + groupDetails.getGroupName());
        actionBar.setDisplayShowTitleEnabled(true);
        Resources res = getResources();
        Bitmap image = CommonUtility.drawImage(groupDetails.getGroupName(), SubGroupPage.this);
        BitmapDrawable icon = new BitmapDrawable(res, image);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(icon);
        lvSubGroplist = (NonScrollListView) findViewById(R.id.list_group);
        lvAccountDetails = (NonScrollListView) findViewById(R.id.list_account);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        accountsList = new ArrayList<>();
        subgroups = new ArrayList<>();
        if (groupDetails.getSubGroupDetails() != null)
            subgroups = groupDetails.getSubGroupDetails();
        accountsList = groupDetails.getAccountDetails();
        adapter = new TrialBalanceAdapter(subgroups, ctx, true);
        accountDetailsAdapter = new AccountDetailsAdapter(accountsList, ctx);
        if (accountsList != null && accountsList.size() > 0) {
            lvAccountDetails.setAdapter(accountDetailsAdapter);
        }
        if (subgroups != null && subgroups.size() > 0) {
            lvSubGroplist.setAdapter(adapter);
        }
        lvSubGroplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                groupDetails = subgroups.get(position);
                //Object obj = lnGroplist.getSelectedItem();
                if (groupDetails.getAccountDetails() != null || groupDetails.getSubGroupDetails() != null) {
                    Intent intent = new Intent(ctx, SubGroupPage.class);
                    intent.putExtra(VariableClass.Vari.SELECTEDDATA, groupDetails);
                    startActivity(intent);
                }
            }
        });
    }
}

