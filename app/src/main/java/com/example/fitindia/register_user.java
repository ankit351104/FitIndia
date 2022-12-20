package com.example.fitindia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class register_user extends AppCompatActivity {
    Button Google, Facebook, phone;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;
    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //FacebookSdk.sdkInitialize(register_user.this);

        setContentView(R.layout.activity_register_user);
        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Intent intent = new Intent(register_user.this, homePage.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(@NotNull FacebookException exception) {
                        // App code
                    }
                });

        Google = findViewById(R.id.Google);
        Google.setVisibility(View.VISIBLE);
        Google.setBackgroundColor(Color.TRANSPARENT);
        Facebook = findViewById(R.id.Facebook);
        Facebook.setVisibility(View.VISIBLE);
        Facebook.setBackgroundColor(Color.TRANSPARENT);
        phone = findViewById(R.id.phone);
        phone.setVisibility(View.VISIBLE);
        phone.setBackgroundColor(Color.TRANSPARENT);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(register_user.this, Arrays.asList("public_profile"));
                Toast.makeText(register_user.this, "Signing you up via Facebook", Toast.LENGTH_SHORT).show();
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(register_user.this, "Signing you up via Phone Number", Toast.LENGTH_SHORT).show();

            }
        });
        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                //Toast.makeText(register_user.this, "Signing you up via Google", Toast.LENGTH_SHORT).show();

            }
        });


        /*if ()
        {
            Intent intent = new Intent(this, homePage.class);
            this.startActivity (intent);
            this.finishActivity (0);
        }*/

        SharedPreferences preferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String FirstTime = preferences.getString("First Install", "");

        if (FirstTime.equals("YES")) {
            Intent intent = new Intent(this, homePage.class);
            this.startActivity(intent);
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("First Install", "YES");
            editor.apply();


        }

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        //LoginManager.getInstance().logInWithReadPermissions(register_user.this, Arrays.asList("public_profile"));

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Toast.makeText(this, "Welcome "+personName, Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(register_user.this,homePage.class));

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Log.d("Sign in Failed", e.toString());
        }
        /*phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(register_user.this, "Signing you up via Phone Number", Toast.LENGTH_SHORT).show();

            }
        });*/
    }
    }