package com.Giddh.ui.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.GroupsSummaryAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.ClipRevealFrame;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.FontTextView;
import com.Giddh.commonUtilities.NonScrollListView;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.EntryInfo;
import com.Giddh.dtos.GroupInfo;
import com.Giddh.dtos.SummaryAccount;
import com.Giddh.dtos.SummaryGroup;
import com.Giddh.dtos.TripInfo;
import com.Giddh.util.AnimatorUtils;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.ogaclejapan.arclayout.ArcLayout;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SummaryInfo extends AppCompatActivity {
    Context ctx;
    ActionBar actionBar;
    TextView tvExpense,
            tvAccTotal, tvamontLiabVal;
    FontTextView tvincomeVal, tvExpenseVal, tvTotalvalueIE, tvAccVal, tvTotalEI;
    UserService userService;
    RelativeLayout rlIncome, rlExpense;
    NonScrollListView lvaccAssets, lvaccLiab;
    ArrayList<EntryInfo> sumList;
    GroupsSummaryAdapter adapter, adapter2;
    SummaryGroup summaryGroup;
    ArrayList<SummaryAccount> summaryAccounts;
    FrameLayout mRootLayout;
    ClipRevealFrame mMenuLayout;
    ImageButton revealButton;
    ArcLayout mArcLayout;
    Button mCenterItem;
    Boolean menu = false;
    LinearLayout mSummary_root;
    LinearLayout home, transactions, settings, trips;
    LinearLayout liabview;
    TripInfo tripInfo;
    ArrayList<TripInfo> savedTrips;
    ArrayList<TripInfo> tripsInDb;
    private static long back_pressed;
    DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testarc);
        ctx = SummaryInfo.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        userService = UserService.getUserServiceInstance(ctx);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
        FontTextView mTitleTextView = (FontTextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("Summary");
        final ImageButton imageButton = (ImageButton) mCustomView
                .findViewById(R.id.imageView1);
        imageButton.setBackgroundResource(R.drawable.menu_actionbar);
        final FrameLayout btnlayout = (FrameLayout) mCustomView
                .findViewById(R.id.button_layout);
        btnlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRevealClick(view);
                if (!menu) {
                    menu = true;
                    imageButton.setBackgroundResource(R.drawable.close_white);
                    mSummary_root.setVisibility(View.GONE);
                } else {
                    menu = false;
                    imageButton.setBackgroundResource(R.drawable.menu_actionbar);
                    mSummary_root.setVisibility(View.VISIBLE);
                }
            }
        });
        decimalFormat = new DecimalFormat("#.00");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        home = (LinearLayout) findViewById(R.id.home_button);
        transactions = (LinearLayout) findViewById(R.id.transaction_button);
        trips = (LinearLayout) findViewById(R.id.trip_button);
        mSummary_root = (LinearLayout) findViewById(R.id.summary_root);
        settings = (LinearLayout) findViewById(R.id.settings_button);
        mRootLayout = (FrameLayout) findViewById(R.id.root_layout);
        mMenuLayout = (ClipRevealFrame) findViewById(R.id.menu_layout);
        mArcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        revealButton = (ImageButton) findViewById(R.id.reveal_button);
        mCenterItem = (Button) findViewById(R.id.center_item);
        tvAccTotal = (TextView) findViewById(R.id.amount_assets);
        tvAccVal = (FontTextView) findViewById(R.id.amount_assets_value);
        liabview = (LinearLayout) findViewById(R.id.list2);
        sumList = new ArrayList<>();
        savedTrips = new ArrayList<>();
        tripsInDb = new ArrayList<>();
        actionBar.setTitle(CommonUtility.getfonttext("Summary", SummaryInfo.this));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));
        lvaccAssets = (NonScrollListView) findViewById(R.id.list_assets);
        tvincomeVal = (FontTextView) findViewById(R.id.income_amount);
        tvExpenseVal = (FontTextView) findViewById(R.id.expense_amount);
        tvExpense = (TextView) findViewById(R.id.income_amount);
        rlIncome = (RelativeLayout) findViewById(R.id.income);
        rlExpense = (RelativeLayout) findViewById(R.id.expe);
        lvaccLiab = (NonScrollListView) findViewById(R.id.list_liab);
        tvTotalvalueIE = (FontTextView) findViewById(R.id.total_amount_in_ex_value);
        tvTotalEI = (FontTextView) findViewById(R.id.total_amount_in_ex);
        tvamontLiabVal = (FontTextView) findViewById(R.id.amount_liab_value);

        Double income = userService.getSumExpenceIncomeEntry("transactionType", "0");
        Double expense = userService.getSumExpenceIncomeEntry("transactionType", "1");
        Double diff = (income - expense);

        tvExpenseVal.setText("" + decimalFormat.format(expense) + " " + Prefs.getCurrency(ctx));
        tvincomeVal.setText("" + decimalFormat.format(income) + " " + Prefs.getCurrency(ctx));
        if (diff < 0) {
            tvTotalEI.setText("Loss");
        } else {
            if (diff == 0) {
                tvTotalEI.setText("TOTAL");
            } else
                tvTotalEI.setText("Profit");
        }
        if (diff < 0)
            diff = diff * -1;
        tvTotalvalueIE.setText("" + decimalFormat.format(diff));
        getclosingBal();
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, HomeActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
                SummaryInfo.this.finish();
            }
        });
        trips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userService.getallTripInfo(null, true).size() > 0) {
                    Intent multi = new Intent(ctx, SavedTrips.class);
                    multi.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(multi);
                    SummaryInfo.this.finish();
                } else {
                    Intent intent = new Intent(ctx, TripHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // SummaryInfo.this.finish();
                }
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, SettingsPage.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
                SummaryInfo.this.finish();
            }
        });
        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, AskType.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
                SummaryInfo.this.finish();
            }
        });
        rlIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SummaryAccount summaryAcc = new SummaryAccount();
                summaryAcc.setTransactionType("0");
                summaryAcc.setAccountName("Income");
                summaryAcc.setGroup(true);
                Intent detail = new Intent(ctx, SummaryDetails.class);
                detail.putExtra(VariableClass.Vari.SELECTEDDATA, summaryAcc);
                ctx.startActivity(detail);
            }
        });
        rlExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SummaryAccount summaryAcc = new SummaryAccount();
                summaryAcc.setTransactionType("1");
                summaryAcc.setAccountName("Expense");
                summaryAcc.setGroup(true);
                Intent detail = new Intent(ctx, SummaryDetails.class);
                detail.putExtra(VariableClass.Vari.SELECTEDDATA, summaryAcc);
                ctx.startActivity(detail);
            }
        });
        adapter = new GroupsSummaryAdapter(summaryAccounts, ctx, false);
        lvaccAssets.setAdapter(adapter);
        adapter2 = new GroupsSummaryAdapter(summaryAccounts, ctx, true);
        lvaccLiab.setAdapter(adapter2);
        if (userService.getaccounts("2").size() == 0) {
            liabview.setVisibility(View.GONE);
            tvamontLiabVal.setVisibility(View.GONE);
        }
        // sumList.get(0);
        List<String> list_spinner = new ArrayList<String>();
        list_spinner.add("Monthly");
        list_spinner.add("Quarterly");
        list_spinner.add("Half Yearly");
        list_spinner.add("Yearly");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, list_spinner);
        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        lvaccAssets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SummaryAccount summaryAcc;
                summaryAcc = (SummaryAccount) parent.getItemAtPosition(position);
                Log.e("clicked item", "" + summaryAcc.getAccountId());
                Intent detail = new Intent(ctx, SummaryDetails.class);
                detail.putExtra(VariableClass.Vari.SELECTEDDATA, summaryAcc);
                ctx.startActivity(detail);
            }
        });
        lvaccLiab.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SummaryAccount summaryAcc;
                summaryAcc = (SummaryAccount) parent.getItemAtPosition(position);
                Log.e("clicked item", "" + summaryAcc.getAccountId());
                Intent detail = new Intent(ctx, SummaryDetails.class);
                detail.putExtra(VariableClass.Vari.SELECTEDDATA, summaryAcc);
                ctx.startActivity(detail);
                SummaryInfo.this.finish();
            }
        });
      /*  lvaccAssets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final SummaryAccount itemAcc = (SummaryAccount) parent.getItemAtPosition(position);
                new AlertDialogWrapper.Builder(ctx)
                        .setTitle("Remove " + itemAcc.getAccountName() + "?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Boolean DefualtName = false;
                        String[] accNames = getResources().getStringArray(R.array.accountName);
                        for (int i = 0; i < accNames.length; i++) {
                            if (itemAcc.getAccountName().equals(accNames[i])) {
                                DefualtName = true;
                                break;
                            }
                        }
                        if (!DefualtName && userService.getEntryInfo(itemAcc.getAccountId()).size() == 0) {
                            userService.delete_account(itemAcc.getAccountId());
                            Intent i = new Intent(ctx, SummaryInfo.class);
                            startActivity(i);
                        } else {
                            CommonUtility.showCustomAlertForContactsError(ctx, "Account in use");
                        }
                    }
                }).show();
                return true;
            }
        });*/
        if (CommonUtility.isNetworkAvailable(ctx)) {
            new tripList().execute();
        }

    }

    private ArrayList<SummaryGroup> getclosingBal() {
        ArrayList<GroupInfo> groups = new ArrayList<>();
        ArrayList<Accounts> acconts = new ArrayList<>();
        ArrayList<SummaryGroup> closings = new ArrayList<>();
        SummaryGroup summary;
        summaryAccounts = new ArrayList<>();
        groups = userService.getgroupInfo();
        for (int i = 0; i < groups.size(); i++) {
            summaryGroup = new SummaryGroup();
            summaryGroup.setGroupName(groups.get(i).getGroupName());
            double closingBalGrp = 0;
            acconts = userService.getaccounts(groups.get(i).getParentId());
            for (int j = 0; j < acconts.size(); j++) {
                Double sumDebit = 0D;
                Double sumCredit = 0D;
                SummaryAccount summaryAccount = new SummaryAccount();
                summaryAccount.setGroupName(groups.get(i).getGroupName());
                summaryAccount.setAccountName(acconts.get(j).getAccountName());
                summaryAccount.setAccountId(String.valueOf(acconts.get(j).getAcc_webId()));
                Double tmpDebitBalance = 0D;
                tmpDebitBalance = userService.getSumExpenceIncomeEntry("debit_account",
                        String.valueOf(acconts.get(j).getAcc_webId()));
                String tmpDebitAccountName = acconts.get(j).getAccountName();
                Double tmpCreditBalance = 0D;
                tmpCreditBalance = userService.getSumExpenceIncomeEntry("creadit_account",
                        String.valueOf(acconts.get(j).getAcc_webId()));
                String tmpCreditAccountName = acconts.get(j).getAccountName();
                sumDebit = tmpDebitBalance;
                sumCredit = tmpCreditBalance;
                Log.e("tmpDebitBalance", "Account Name = " + tmpDebitAccountName + ", balance = " + tmpDebitBalance);
                Log.e("tmpCreditBalance", "Account Name = " + tmpCreditAccountName + ", balance = " + tmpCreditBalance);
                double v = (sumDebit - sumCredit) - acconts.get(j).getOpeningBalance();
                summaryAccount.setClosingBal(v);
                closingBalGrp = closingBalGrp + v;
                summaryAccounts.add(summaryAccount);
            }
            if (summaryGroup.getGroupName().equals("Assets")) {
                tvAccVal.setText(decimalFormat.format(closingBalGrp));
            } else if (summaryGroup.getGroupName().equals("Liability")) {
                tvamontLiabVal.setText(decimalFormat.format(closingBalGrp * -1));
               /* if (closingBalGrp < 0)
                    tvamontLiabVal.setText(String.valueOf(closingBalGrp * -1));
                else
                    tvamontLiabVal.setText(String.valueOf(closingBalGrp));*/
            }
            //   Log.e("Total Debit Sum", "Total sum = "+ sumDebit);
            // Log.e("Total Credit Sum", "Total sum = "+ sumCredit);
            summaryGroup.setAccounts(summaryAccounts);
            summaryGroup.setClosingBal((Double) closingBalGrp);
            Log.e("Group Name", "groupName = " + summaryGroup.getGroupName() + ", Closing Balance" + summaryGroup.getClosingBal());
            closings.add(summaryGroup);
        }
        return closings;
    }

    void onRevealClick(View v) {
        int x = (v.getLeft() + v.getRight()) / 2;
        int y = (v.getTop() + v.getBottom()) / 2;
        float radiusOfFab = 1f * v.getWidth() / 2f;
        float radiusFromFabToRoot = (float) Math.hypot(
                Math.max(x, mRootLayout.getWidth() - x),
                Math.max(y, mRootLayout.getHeight() - y));
        if (v.isSelected()) {

            hideMenu(x, y, radiusFromFabToRoot, radiusOfFab);
            // revealButton.setImageResource(0);
        } else {

            showMenu(x, y, radiusOfFab, radiusFromFabToRoot);
            //  revealButton.setImageResource(R.drawable.close);
        }
        v.setSelected(!v.isSelected());
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
                    SummaryInfo.this.finish();
                }
            }).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    class tripList extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;
        JSONArray japarent = null;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // showErrorMessage(false, "");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).getTripList();
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        userService.deleteAlltrips();
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        if (japarent.length() > 0)
                            for (int i = 0; i < japarent.length(); i++) {
                                tripInfo = new TripInfo();
                                jochild = japarent.getJSONObject(i);
                                tripInfo.setTripId(jochild.getString(VariableClass.ResponseVariables.TRIP_ID));
                                tripInfo.setOwner(jochild.getString(VariableClass.ResponseVariables.OWNER));
                                tripInfo.setTripName(jochild.getString(VariableClass.ResponseVariables.TRIP_NAME));
                                savedTrips.add(tripInfo);
                                tripsInDb = userService.getallTripInfo(tripInfo.getTripId(), false);
                                if (savedTrips != null)
                                    tripInfo.setSavedTrips(savedTrips);
                                if (tripsInDb.size() > 0) {
                                    userService.updateTripInfo(tripInfo, tripInfo.getTripId());
                                } else {
                                    userService.addTripInfo(tripInfo);
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

    @Override
    protected void onPause() {
        super.onPause();
        new tripList().cancel(true);
    }
}
