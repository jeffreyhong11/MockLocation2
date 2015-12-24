package com.example.jeffrey.mocklocation2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


/**
 * Created by Jeffrey on 12/20/2015.
 */
public class LoginActivity extends Activity {

    LoginButton fb_loginButton;
    TextView sqlLogon;
    CallbackManager callbackManager;
    TinyDB tiny;
    ProgressDialog pDialog;
    final String LOGIN_URL = "http://65.60.184.132/webservice/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FacebookSdk.sdkInitialize(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);


        //attempts to determine if a user that had previously logged in had executed a log out order from the MainActivity
        //if so, then this code will log them out from Facebook

        try{
            Bundle extras = getIntent().getExtras();
            if (extras.getString("MockLocation_logout").equals("true")){
                LoginManager.getInstance().logOut();
            }
        }catch (Exception e){}

        tiny = new TinyDB(this);

        fb_loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        fb_loginButton.setReadPermissions("public_profile");

        callbackManager = CallbackManager.Factory.create();

        fb_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            //if a user succesfully logs in, they will obtain a loginToken

            @Override
            public void onSuccess(LoginResult loginResult) {
                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            Log.v("facebook - profile", profile2.getId() + "hhh");
                            loginToken(profile2);
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                } else {
                    Profile profile = Profile.getCurrentProfile();
                    Log.v("facebook - profile", profile.getId() + "hhh");
                    loginToken(profile);
                }

            }

            @Override
            public void onCancel() {
                // App code
                Log.i("tag", "fb login canceled" + "hhh");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("tag", "fb login error" + "hhh");
            }

        });


    }



    //callbackManager for the facebook login button

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    //this creates a login token for the user based on their facebook ID

    public void loginToken (Profile profile){
        tiny.putString("MockLocation_login", profile.getId().toString() + ":" + "42");

        Intent i = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(i);

        finish();
    }

}
