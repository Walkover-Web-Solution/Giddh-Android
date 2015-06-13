package com.Giddh.ui.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Giddh.R;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.splunk.mint.Mint;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignUpHome extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
    TextView btnfacebookLogin;
    private final static String APP_ID = "830265573699296";
    private Boolean mConfirmCredentials = false;
    private static final String TAG = "SignUpHome";
    public static final int ACCESSTYPEFB = 1;
    public static final int ACCESSTYPEGOOGLE = 2;
    public static final int REQUEST_CODE = 01;
    private static final int RC_SIGN_IN = 0;

    // Google client to communicate with Google
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;
    private boolean signedInUser;
    private ConnectionResult mConnectionResult;
    private SignInButton signinButton;
    String full_name;
    String user_email, mAccountName;
    Context ctx;
    static int accessType;
    private SimpleFacebook mSimpleFacebook;
    Permission[] permissions = new Permission[]{
            Permission.PUBLIC_PROFILE,
            Permission.EMAIL,
    };
    static String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_home);
        ctx = this;
        Mint.initAndStartSession(SignUpHome.this, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(SignUpHome.this));
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/fontawesome_webfont.ttf");
        btnfacebookLogin = (TextView) findViewById(R.id.btn_fb_login);
        /*btnGoogleLogin = (TextView) findViewById(R.id.btn_google_login);*/
        signinButton = (SignInButton) findViewById(R.id.btn_google_login);
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(APP_ID)
                .setNamespace("Utteru")
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessType = ACCESSTYPEGOOGLE;
                googlePlusLogin();

            }
        });
        btnfacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showErrorMessage(false, "");
                if (CommonUtility.isNetworkAvailable(ctx)) {
                    accessType = ACCESSTYPEFB;
                    mSimpleFacebook.login(onLoginListener);
                    //mSimpleFacebook.getProfile(onProfileListener);
                } else {
                    showErrorMessage(true, "No network");
                }
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();


    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void signIn(View v) {
        googlePlusLogin();
    }

    public void logout(View v) {
        googlePlusLogout();
    }

    public void googlePlusLogout() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        signedInUser = false;
        getProfileInformation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("activity result", "activity result ");

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                signedInUser = false;

            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }


        }
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        //super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
               /* String personPhotoUrl = currentPerson.getImage().getUrl();*/
                Prefs.setUserName(SignUpHome.this, personName);
                mAccountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Prefs.setEmailId(ctx, mAccountName);
                if (VariableClass.Vari.LOGIN_FIRST_TIME) {
                    AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... params) {
                            String token = null;

                            try {
                                accessToken = GoogleAuthUtil.getToken(
                                        SignUpHome.this,
                                        mAccountName,
                                        "oauth2:profile email");
                            } catch (IOException transientEx) {
                                // Network or server error, try later
                                Log.e(TAG, transientEx.toString());
                            } catch (UserRecoverableAuthException e) {
                                // Recover (with e.getIntent())
                                Log.e(TAG, e.toString());
                                Intent recover = e.getIntent();
                                startActivityForResult(recover, RC_SIGN_IN);
                            } catch (GoogleAuthException authEx) {
                                // The call is not ever expected to succeed
                                // assuming you have already verified that
                                // Google Play services is installed.
                                Log.e(TAG, authEx.toString());
                            }

                            return accessToken;
                        }

                        @Override
                        protected void onPostExecute(String token) {
                            Log.i(TAG, "Access token retrieved:" + token);
                            new LoginGoogleFb().execute();
                        }

                    };
                    task.execute();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final OnLoginListener onLoginListener = new OnLoginListener() {
        @Override
        public void onLogin() {
            // change the state of the button or do whatever you want
            accessToken = mSimpleFacebook.getSession().getAccessToken();
            Log.e("facebook token", "" + accessToken);
            // Toast.makeText(ctx,""+accessToken,Toast.LENGTH_SHORT).show();
            //check validation
            mSimpleFacebook.getProfile(onProfileListener);
            new LoginGoogleFb().execute(null, null, null);
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {
            // user didn't accept READ or WRITE permission
            Log.w(TAG, String.format("You didn't accept %s permissions", type.name()));
        }

        @Override
        public void onThinking() {
            Log.w(TAG, String.format("You didn't accept %s permissions", "thinking"));
        }

        @Override
        public void onException(Throwable throwable) {
            Log.e("reason", String.valueOf(throwable));
        }

        @Override
        public void onFail(String reason) {
            Log.e("reason", reason);
        }
    };
    final OnProfileListener onProfileListener = new OnProfileListener() {
        @Override
        public void onComplete(Profile profile) {
            Prefs.setCurrency(ctx, profile.getCurrency());
            accessToken = mSimpleFacebook.getSession().getAccessToken();
            full_name = profile.getFirstName() + " " + profile.getLastName();
            //Prefs.setUserName(ctx, full_name);
            user_email = profile.getEmail();
            Prefs.setEmailId(ctx, user_email);
            Log.e("name", user_email + full_name);
        }

        @Override
        public void onException(Throwable throwable) {
            Log.e("", "" + throwable);
            super.onException(throwable);
        }

        @Override
        public void onFail(String reason) {
            Log.e("1", "" + reason);
            super.onFail(reason);
            Log.e("2", "" + reason);
        }
            /*
     * You can override other methods here:
     * onThinking(), onFail(String reason), onException(Throwable throwable)
     */
    };

    void showErrorMessage(Boolean showm, String message) {
       /* if (showm) {
            error_textview.setText(message);
            if (error_layout.getVisibility() == View.GONE)
                CommonUtility.expand(error_layout);
        } else {
            if (error_layout.getVisibility() == View.VISIBLE)
                CommonUtility.collapse(error_layout);
        }*/
    }

    class LoginGoogleFb extends AsyncTask<Void, Void, Void> {
        String response = "";
        Boolean iserr = false;
        JSONObject parent, child;
        JSONArray jarray;

        @Override
        protected void onPreExecute() {
            CommonUtility.show_PDialog(SignUpHome.this);
            btnfacebookLogin.setEnabled(true);
            //  btnGoogleLogin.setEnabled(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).signupWithGoogleFacebook(accessToken, "" + accessType);
            if (!response.equalsIgnoreCase("")) {
                try {
                    parent = new JSONObject(response);
                    //if response of failed
                    if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        child = parent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = child.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //if response of success
                    else if (parent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        Log.e("success response", "success respo");
                        String msg = parent.getString(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        jarray = parent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        String Authkey = jarray.getString(0);
                        Prefs.setAuthkey(ctx, Authkey);
                        Log.e("Auth key", "" + Authkey);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (iserr) {
                showErrorMessage(true, response);
            } else {
                Intent startmenu = new Intent(ctx, HomeActivity.class);
                startmenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startmenu);
            }
            CommonUtility.dialog.dismiss();
            btnfacebookLogin.setEnabled(true);
            // btnGoogleLogin.setEnabled(true);
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }

        if (mGoogleApiClient.isConnected()) {

            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        if (!mIntentInProgress) {

            mConnectionResult = result;

            if (signedInUser) {

                resolveSignInError();
            }
        }

    }

    private void googlePlusLogin() {
        if (!mGoogleApiClient.isConnecting()) {
            signedInUser = true;
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (CommonUtility.dialog != null) {
            CommonUtility.dialog.dismiss();
        }
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            //mGoogleApiClient.connect();
        }
        super.onDestroy();
    }
}