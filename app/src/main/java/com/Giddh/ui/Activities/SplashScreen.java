package com.Giddh.ui.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.Giddh.R;
import com.Giddh.adapters.CompanyListAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.Company;
import com.Giddh.dtos.GroupInfo;
import com.Giddh.util.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 2000;
    Context ctx;
    Company selecteddto;
    ArrayList<Company> companies = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        ctx = SplashScreen.this;
        companies = new ArrayList<>();
        //  Mint.initAndStartSession(SplashScreen.this, CommonUtility.BUGSENSEID);
        // Mint.setUserIdentifier(Prefs.getUserDefaultNumber(SplashScreen.this));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                String Authkey = Prefs.getAuthkey(SplashScreen.this);
                if (!Authkey.equals("") && !(Authkey == null)) {
                    VariableClass.Vari.LOGIN_FIRST_TIME = false;
                    VariableClass.Vari.MESSEGE_FIRST_TIME = false;
                    VariableClass.Vari.FIRST_TIME_ENTRY = false;
                    Log.e("sizelist",""+Prefs.getCompanysize(ctx));
                    if (Prefs.getCompanysize(ctx).equals("1")) {
                        Intent localIntent = new Intent(SplashScreen.this, AskType.class);
                        SplashScreen.this.startActivity(localIntent);
                        SplashScreen.this.finish();
                    } else {
                        Intent localIntent = new Intent(SplashScreen.this, HomeActivity.class);
                        SplashScreen.this.startActivity(localIntent);
                        SplashScreen.this.finish();
                    }

                } else {
                    VariableClass.Vari.LOGIN_FIRST_TIME = true;
                    VariableClass.Vari.MESSEGE_FIRST_TIME = true;
                    VariableClass.Vari.FIRST_TIME_ENTRY = true;
                    Intent localIntent = new Intent(SplashScreen.this, SignUpHome.class);
                    SplashScreen.this.startActivity(localIntent);
                    SplashScreen.this.finish();
                }
            }
        }
                , SPLASH_TIME_OUT);
    }
}
