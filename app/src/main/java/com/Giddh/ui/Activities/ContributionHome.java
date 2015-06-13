package com.Giddh.ui.Activities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.Giddh.R;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.TripInfo;
import com.Giddh.util.UserService;
import com.splunk.mint.Mint;

/**
 * Created by walkover on 23/4/15.
 */
public class ContributionHome extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;
    TripInfo tripInfo;
    public static String[] CONTENT = new String[]{"Trip Partners", "Summary"};
    Context ctx;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contrihome);
        ctx = ContributionHome.this;
        userService = UserService.getUserServiceInstance(ctx);
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        tripInfo = (TripInfo) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(CommonUtility.getfonttext(tripInfo.getTripName().toString(), ContributionHome.this));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ContriAdapter(getSupportFragmentManager()));
    }

    private class ContriAdapter extends FragmentPagerAdapter {
        public ContriAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return SavedEmails.newInstance(0, "Trip Partners");
                case 1:
                    return PerHeadContri.newInstance(1, "Individual Summary");
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return 2;
        }
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