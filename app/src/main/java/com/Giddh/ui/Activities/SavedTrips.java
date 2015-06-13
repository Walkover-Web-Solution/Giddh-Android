package com.Giddh.ui.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.SavedTripsAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.ClipRevealFrame;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.TripInfo;
import com.Giddh.util.AnimatorUtils;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ogaclejapan.arclayout.ArcLayout;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SavedTrips extends AppCompatActivity {
    ListView lvSavedTrips;
    ImageButton addNewTrip;
    UserService userService;
    Context ctx;
    ArrayList<TripInfo> savedTrips;
    SavedTripsAdapter savedTripsAdapter;
    EntryInfo entryInfo;
    TripInfo info;
    ActionBar actionBar;
    LinearLayout home, transactions, settings, trips;
    FrameLayout mRootLayout;
    ClipRevealFrame mMenuLayout;
    ImageButton revealButton;
    ArcLayout mArcLayout;
    Button mCenterItem;
    Boolean menu = false;
    RelativeLayout mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_layout);
        init();
        for (int i = 0; i < savedTrips.size(); i++) {
            new getSummaryTrip().execute(savedTrips.get(i).getTripId());
        }
    }

    void init() {
        ctx = SavedTrips.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("Saved Trips");
        final ImageButton imageButton = (ImageButton) mCustomView
                .findViewById(R.id.imageView1);
        imageButton.setBackgroundResource(R.drawable.menu_actionbar);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        settings = (LinearLayout) findViewById(R.id.settings_button);
        home = (LinearLayout) findViewById(R.id.home_button);
        transactions = (LinearLayout) findViewById(R.id.transaction_button);
        trips = (LinearLayout) findViewById(R.id.trip_button);
        mRootLayout = (FrameLayout) findViewById(R.id.root_layout);
        mMenuLayout = (ClipRevealFrame) findViewById(R.id.menu_layout);
        mArcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        revealButton = (ImageButton) findViewById(R.id.reveal_button);
        mCenterItem = (Button) findViewById(R.id.center_item);
        mRootView = (RelativeLayout) findViewById(R.id.root);
        savedTrips = new ArrayList<>();
        userService = UserService.getUserServiceInstance(ctx);
        savedTrips = userService.getallTripInfo(null, true);
        lvSavedTrips = (ListView) findViewById(R.id.saved_trips);
        addNewTrip = (ImageButton) findViewById(R.id.add_new_trip);
        savedTripsAdapter = new SavedTripsAdapter(savedTrips, ctx);
        lvSavedTrips.setAdapter(savedTripsAdapter);
        addNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create = new Intent(ctx, TripHome.class);
                startActivity(create);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, HomeActivity.class);
                startActivity(home);
            }
        });
        trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent multi = new Intent(ctx, SummaryInfo.class);
                startActivity(multi);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, SettingsPage.class);
                startActivity(home);
            }
        });
        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, AskType.class);
                startActivity(home);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRevealClick(view);
                if (!menu) {
                    menu = true;
                    imageButton.setBackgroundResource(R.drawable.close_white);
                    mRootView.setVisibility(View.GONE);
                } else {
                    menu = false;
                    mRootView.setVisibility(View.VISIBLE);
                    imageButton.setBackgroundResource(R.drawable.menu_actionbar);
                }
            }
        });
        lvSavedTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showdetails = new Intent(ctx, ContributionHome.class);
                showdetails.putExtra(VariableClass.Vari.SELECTEDDATA, (TripInfo) parent.getItemAtPosition(position));
                startActivity(showdetails);
            }
        });
        lvSavedTrips.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                info = (TripInfo) arg0.getItemAtPosition(pos);
                new MaterialDialog.Builder(ctx)
                        .items(R.array.exit_trip)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                new AlertDialogWrapper.Builder(ctx)
                                        .setTitle("do you want to exit?")
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new exitTrip().execute(info.getTripId());
                                        savedTrips.remove(arg0.getItemAtPosition(pos));
                                        userService.deleteTrip(info.getTripId());
                                        lvSavedTrips.setAdapter(savedTripsAdapter);
                                        lvSavedTrips.deferNotifyDataSetChanged();
                                    }
                                }).show();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    class getSummaryTrip extends AsyncTask<String, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            if (iserr) {
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
            String Entryid = userService.getEntryIdtrip(params[0]);
            response = Apis.getApisInstance(ctx).getTripSummary(params[0], Entryid);
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                JSONArray jachild = null;
                JSONObject jobjin = null;
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
                        if (japarent.length() > 0)
                            for (int i = 0; i < japarent.length(); i++) {
                                entryInfo = new EntryInfo();
                                jochild = japarent.getJSONObject(i);
                                //   entryInfo.setTransactionType(jochild.getString(VariableClass.ResponseVariables.TRANSACTIONTYPE));
                                entryInfo.setEmail(jochild.getString(VariableClass.ResponseVariables.EMAIL));
                                entryInfo.setCompanyId(jochild.getString(VariableClass.ResponseVariables.COMPANY_ID));
                                //  if (!entryInfo.getEmail().equals(Prefs.getEmailId(ctx))) {
                                entryInfo.setTripId(params[0]);
                                jachild = new JSONArray();
                                jobjin = new JSONObject();
                                jachild = jochild.getJSONArray(VariableClass.ResponseVariables.DEBIT);
                                jobjin = jachild.getJSONObject(0);
                                entryInfo.setAmount(Double.parseDouble(jobjin.getString(VariableClass.ResponseVariables.AMOUNT)));
                                entryInfo.setDebitAccount(jobjin.getString(VariableClass.ResponseVariables.ACCOUNT_NAME));
                                String grpDebit = jobjin.getString(VariableClass.ResponseVariables.GROUP_NAME);
                                entryInfo.setDate(jochild.getString(VariableClass.ResponseVariables.ENTRY_DATE));
                                jachild = jochild.getJSONArray(VariableClass.ResponseVariables.CREDIT);
                                jobjin = jachild.getJSONObject(0);
                                entryInfo.setCreditAccount(jobjin.getString(VariableClass.ResponseVariables.ACCOUNT_NAME));
                                entryInfo.setEntryId(jochild.getString(VariableClass.ResponseVariables.ENTRY_ID));
                                entryInfo.setTransactionType("0");
                                if (grpDebit.toLowerCase().contains("income")|| jobjin.getString(VariableClass.ResponseVariables.GROUP_NAME).toLowerCase().contains("income") || jobjin.getString(VariableClass.ResponseVariables.GROUP_NAME).toLowerCase().contains("liability") ) {
                                    entryInfo.setTransactionType("0");
                                }
                                if (jobjin.getString(VariableClass.ResponseVariables.GROUP_NAME).toLowerCase().contains("assets") && grpDebit.toLowerCase().contains("assets")) {
                                    entryInfo.setTransactionType("0");
                                }
                                if (grpDebit.toLowerCase().contains("expense") || jobjin.getString(VariableClass.ResponseVariables.GROUP_NAME).toLowerCase().contains("expense") || grpDebit.toLowerCase().contains("liability"))
                                    entryInfo.setTransactionType("1");

                                userService.addTripentrydata(entryInfo);
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

    class exitTrip extends AsyncTask<String, Void, Void> {
        String response = null;
        Boolean iserr = false;
        String id;

        @Override
        protected void onPostExecute(Void result) {
            // CommonUtility.dialog.dismiss();
            if (iserr) {
                //showErrorMessage(true, response);
            } else {
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
        protected Void doInBackground(String... params) {
            id = params[0];
            response = Apis.getApisInstance(ctx).exitTrip(params[0]);
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

    void onRevealClick(View v) {
        int x = (v.getLeft() + v.getRight()) / 2;
        int y = (v.getTop() + v.getBottom()) / 2;
        float radiusOfFab = 1f * v.getWidth() / 2f;
        float radiusFromFabToRoot = (float) Math.hypot(
                Math.max(x, mRootLayout.getWidth() - x),
                Math.max(y, mRootLayout.getHeight() - y));
        if (v.isSelected()) {
            disableChilds();
            hideMenu(x, y, radiusFromFabToRoot, radiusOfFab);
            revealButton.setImageResource(0);
        } else {
            enableChilds();
            showMenu(x, y, radiusOfFab, radiusFromFabToRoot);
            //  revealButton.setImageResource(R.drawable.close);
        }
        v.setSelected(!v.isSelected());
    }

    void disableChilds() {
       /* for (int i = 0; i < mArcLayout.getChildCount(); i++) {
            View child = mArcLayout.getChildAt(i);
            child.setClickable(false);
        }*/
    }

    void enableChilds() {
       /* for (int i = 0; i < mArcLayout.getChildCount(); i++) {
            View child = mArcLayout.getChildAt(i);
            child.setClickable(true);
        }*/
    }

    private void showMenu(int cx, int cy, float startRadius, float endRadius) {
        mMenuLayout.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();
        Animator revealAnim = createCircularReveal(mMenuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(200);
        animList.add(revealAnim);
        animList.add(createShowItemAnimator(mCenterItem));
        for (int i = 0, len = mArcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(mArcLayout.getChildAt(i)));
        }
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();
    }

    private void hideMenu(int cx, int cy, float startRadius, float endRadius) {
        List<Animator> animList = new ArrayList<>();
        for (int i = mArcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(mArcLayout.getChildAt(i)));
        }
        animList.add(createHideItemAnimator(mCenterItem));
        Animator revealAnim = createCircularReveal(mMenuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(100);
        revealAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mMenuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animList.add(revealAnim);
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();
    }

    private Animator createShowItemAnimator(View item) {
        float dx = mCenterItem.getX() - item.getX();
        float dy = mCenterItem.getY() - item.getY();
        item.setScaleX(0f);
        item.setScaleY(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(50);
        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        final float dx = mCenterItem.getX() - item.getX();
        final float dy = mCenterItem.getY() - item.getY();
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(1f, 0f),
                AnimatorUtils.scaleY(1f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });
        anim.setDuration(50);
        return anim;
    }

    private Animator createCircularReveal(final ClipRevealFrame view, int x, int y, float startRadius, float endRadius) {
        final Animator reveal;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            reveal = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        } else {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            reveal = ObjectAnimator.ofFloat(view, "ClipRadius", startRadius, endRadius);
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setClipOutLines(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
        return reveal;
    }
}
