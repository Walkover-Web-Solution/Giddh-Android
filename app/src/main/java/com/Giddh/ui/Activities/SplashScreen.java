package com.Giddh.ui.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.Giddh.R;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.GroupInfo;
import com.Giddh.util.UserService;

/**
 * Created by root on 11/29/14.
 */
public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 1500;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        ctx = SplashScreen.this;
        //  Mint.initAndStartSession(SplashScreen.this, CommonUtility.BUGSENSEID);
        // Mint.setUserIdentifier(Prefs.getUserDefaultNumber(SplashScreen.this));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                String Authkey = Prefs.getAuthkey(SplashScreen.this);
                if (!Authkey.equals("") && !(Authkey == null)) {
                    VariableClass.Vari.LOGIN_FIRST_TIME = false;
                    VariableClass.Vari.MESSEGE_FIRST_TIME = false;
                    VariableClass.Vari.FIRST_TIME_ENTRY = false;
                    Intent localIntent = new Intent(SplashScreen.this, HomeActivity.class);
                    SplashScreen.this.startActivity(localIntent);
                    SplashScreen.this.finish();
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
