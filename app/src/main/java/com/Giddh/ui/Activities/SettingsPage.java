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
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.Giddh.R;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.ClipRevealFrame;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.FontTextView;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
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
import java.util.Arrays;
import java.util.List;

public class SettingsPage extends AppCompatActivity implements View.OnClickListener {
    FontTextView tvTrips, tvBankAccounts, tvSyncEntries, tvProfileName, tvCreditCard,
            tvCurrency, tvaccounts, subaccmain,
            subcurrency, subSync, subtrips, subaccounts, subprofilename, subcard;
    Context ctx;
    UserService userService;
    RelativeLayout accounts1, logout1;
    int count;
    FrameLayout mRootLayout;
    ClipRevealFrame mMenuLayout;
    ImageButton revealButton;
    ArcLayout mArcLayout;
    Button mCenterItem;
    Boolean menu = false;
    RelativeLayout mRootView;
    ActionBar actionBar;
    LinearLayout home, transactions, trip, summary;
    private static long back_pressed;
    ArrayList<Accounts> accounts, cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        ctx = SettingsPage.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);
        FontTextView mTitleTextView = (FontTextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("Settings");
        final ImageButton imageButton = (ImageButton) mCustomView
                .findViewById(R.id.imageView1);
        final FrameLayout btnlayout = (FrameLayout) mCustomView
                .findViewById(R.id.button_layout);
        imageButton.setBackgroundResource(R.drawable.menu_actionbar);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
        trip = (LinearLayout) findViewById(R.id.trip_button);
        home = (LinearLayout) findViewById(R.id.home_button);
        transactions = (LinearLayout) findViewById(R.id.transaction_button);
        summary = (LinearLayout) findViewById(R.id.summary_button);
        mRootLayout = (FrameLayout) findViewById(R.id.root_layout);
        mMenuLayout = (ClipRevealFrame) findViewById(R.id.menu_layout);
        mArcLayout = (ArcLayout) findViewById(R.id.arc_layout);
        revealButton = (ImageButton) findViewById(R.id.reveal_button);
        mCenterItem = (Button) findViewById(R.id.center_item);
        mRootView = (RelativeLayout) findViewById(R.id.root);
        accounts = new ArrayList<>();
        cat = new ArrayList<>();
        userService = UserService.getUserServiceInstance(ctx);
      /*  actionBar.setTitle(CommonUtility.getfonttext("Settings", SettingsPage.this));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_footer_head)));*/
        tvTrips = (FontTextView) findViewById(R.id.aso_trip);
        tvBankAccounts = (FontTextView) findViewById(R.id.add_bnk);
        tvCreditCard = (FontTextView) findViewById(R.id.credit_card);
        tvCurrency = (FontTextView) findViewById(R.id.currency);
        tvCurrency.setOnClickListener(this);
        subcurrency = (FontTextView) findViewById(R.id.subcurrency);
        tvaccounts = (FontTextView) findViewById(R.id.accounts);
        subaccmain = (FontTextView) findViewById(R.id.subaccount);
        accounts1 = (RelativeLayout) findViewById(R.id.accounts1);
        logout1 = (RelativeLayout) findViewById(R.id.logout1);
        subaccmain.setText("" + getfilteredlist().size());
        accounts1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trip = new Intent(ctx, SelectInfo.class);
                trip.putExtra(VariableClass.Vari.SELECTEDDATA, 4);
                trip.putExtra("CashInAtm", false);
                startActivity(trip);
                SettingsPage.this.finish();
            }
        });
        logout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialogWrapper.Builder(ctx)
                        .setTitle("Are you sure?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtility.clearData(ctx, SettingsPage.this);
                        VariableClass.Vari.LOGIN_FIRST_TIME = true;
                        VariableClass.Vari.MESSEGE_FIRST_TIME = true;
                        Intent SignUp = new Intent(ctx, SplashScreen.class);
                        SignUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SignUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(SignUp);
                        SettingsPage.this.finish();
                    }
                }).show();
            }
        });
        btnlayout.setOnClickListener(new View.OnClickListener() {
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
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, HomeActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
                SettingsPage.this.finish();
            }
        });
        trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userService.getallTripInfo(null, true).size() > 0) {
                    Intent multi = new Intent(ctx, SavedTrips.class);
                    multi.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(multi);
                    SettingsPage.this.finish();
                } else {
                    Intent intent = new Intent(ctx, TripHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }
        });
        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, SummaryInfo.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
                SettingsPage.this.finish();
            }
        });
        transactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(ctx, AskType.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(home);
                SettingsPage.this.finish();
            }
        });
        tvSyncEntries = (FontTextView) findViewById(R.id.sync);
        subSync = (FontTextView) findViewById(R.id.subSync);
        tvProfileName = (FontTextView) findViewById(R.id.change_name);
        subSync.setText("Last updated: " + Prefs.getUpdateDate(ctx));
        subprofilename = (FontTextView) findViewById(R.id.subprofilename);
        subprofilename.setText(Prefs.getEmailId(ctx));
        subaccounts = (FontTextView) findViewById(R.id.subaccounts);
        tvSyncEntries.setOnClickListener(this);
        tvBankAccounts.setOnClickListener(this);
        tvTrips.setOnClickListener(this);
        tvCreditCard.setOnClickListener(this);
        tvProfileName.setOnClickListener(this);
        accounts = userService.getcountacc("3", "Cash");
        subaccounts.setText("" + accounts.size());
        subcard = (FontTextView) findViewById(R.id.subcard);
        accounts = userService.getcountacc("2", "Loan");
        subcard.setText("" + accounts.size());
        subtrips = (FontTextView) findViewById(R.id.subtrips);
        count = userService.getcount("trip_info");
        subtrips.setText("" + count);
        tvCurrency.setText(Prefs.getCurrency(ctx));
        subcurrency.setText(Prefs.getCountry(ctx));
        tvProfileName.setText(Prefs.getUserName(ctx));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aso_trip:
                Intent multi = new Intent(ctx, SavedTrips.class);
                startActivity(multi);
                break;
            case R.id.add_bnk:
                Intent addbank = new Intent(ctx, AddBankDetails.class);
                addbank.putExtra("value", true);
                startActivity(addbank);
                break;
            case R.id.sync:
                CommonUtility.syncwithServer(ctx);
                CommonUtility.showCustomAlertForContactsError(ctx, "Updated");
                break;
            case R.id.credit_card:
                Intent addcard = new Intent(ctx, AddBankDetails.class);
                addcard.putExtra("value", false);
                startActivity(addcard);
                break;
            case R.id.change_name:
                new MaterialDialog.Builder(ctx)
                        .title("Enter new profile Name")
                        .content("Profile Name will be changed").negativeText("CANCEL").positiveText("OK")
                        .input("ex:John Martin", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.toString() != null && !input.toString().equals("")) {
                                    new ChangeProfileName().execute(input.toString());
                                    Prefs.setUserName(ctx, input.toString());
                                    tvProfileName.setText(Prefs.getUserName(ctx));
                                    subprofilename.setText(Prefs.getEmailId(ctx));
                                }
                            }
                        }).show();
                break;
            case R.id.currency:
                String[] country = getResources().getStringArray(R.array.country_list);
                String[] symbol = getResources().getStringArray(R.array.country_symbol);
                String mixarr[] = new String[country.length];
                for (int i = 0; i < country.length; i++) {
                    String both = country[i] + " (" + symbol[i] + ")";
                    mixarr[i] = both;
                }
                new MaterialDialog.Builder(ctx)
                        .title("Select Currency")
                        .items(mixarr)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text != null) {
                                    tvCurrency.setText(text.toString().substring(text.toString().indexOf("(") + 1, text.toString().indexOf(")")));
                                    Prefs.setCurrency(ctx, text.toString().substring(text.toString().indexOf("(") + 1, text.toString().indexOf(")")));
                                    Prefs.setCountry(ctx, text.toString().substring(0, text.toString().indexOf(" ")));
                                    subcurrency.setText(text.toString().substring(0, text.toString().indexOf(" ")));
                                }
                                return true;
                            }
                        })
                        .positiveText("Ok")
                        .show();
                break;
        }
    }

    class ChangeProfileName extends AsyncTask<String, Void, Void> {
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
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            response = Apis.getApisInstance(ctx).changeProfileName(params[0]);
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

    ArrayList<Accounts> getfilteredlist() {
        cat = userService.getallAccounts("3", false, true);
        ArrayList<Accounts> temp = new ArrayList<>();
        String[] accNames = getResources().getStringArray(R.array.accountName);
        int listsize = cat.size();
        for (int i = 0; i < listsize; i++) {
            if (!Arrays.asList(accNames).contains(cat.get(i)
                    .getAccountName())) {
                temp.add(cat.get(i));
            }
        }
        return temp;
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
//            revealButton.setImageResource(0);
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
                    SettingsPage.this.finish();
                }
            }).show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
