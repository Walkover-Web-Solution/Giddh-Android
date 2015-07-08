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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.Giddh.R;
import com.Giddh.adapters.AccountDetailsAdapter;
import com.Giddh.adapters.TrialBalanceAdapter;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.FontTextView;
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
      /*  actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle(" " + groupDetails.getGroupName());
        actionBar.setDisplayShowTitleEnabled(true);*/
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_subgrp, null);
        FontTextView mTitleTextView = (FontTextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(groupDetails.getGroupName());
        final ImageButton imageButton = (ImageButton) mCustomView
                .findViewById(R.id.imageView1);
        final ImageButton imagemenu = (ImageButton) mCustomView
                .findViewById(R.id.menuimage);
        final FrameLayout btnlayout = (FrameLayout) mCustomView
                .findViewById(R.id.button_layout);
        Resources res = getResources();
        Bitmap image = CommonUtility.drawImage(groupDetails.getGroupName(), SubGroupPage.this);
        BitmapDrawable icon = new BitmapDrawable(res, image);
        imageButton.setImageDrawable(icon);
        imagemenu.setBackgroundResource(R.drawable.company);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        imagemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                SubGroupPage.this.finish();
            }
        });
     /*   actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(icon);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));*/
        lvSubGroplist = (NonScrollListView) findViewById(R.id.list_group);
        lvAccountDetails = (NonScrollListView) findViewById(R.id.list_account);
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

