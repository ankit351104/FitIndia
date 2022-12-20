package com.example.fitindia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class homePage extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener{
    private ImageView profile;
    private TextView name;
    private GoogleApiClient googleApiClient;
    private  GoogleSignInOptions gso;
    TextView greet;
    ImageView feed,notification;
    private TextView counter,steps;
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private boolean isCounterSensorPresent;
//    int stepCount = 0;
    private long step = 0;
    private double MagnitudePrevious=0;
    private  Integer stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        counter = findViewById(R.id.counter);
        steps = findViewById(R.id.steps);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener stepDetector = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent != null) {
                    float x_acceleration = sensorEvent.values[0];
                    float y_acceleration = sensorEvent.values[1];
                    float z_acceleration = sensorEvent.values[2];

                    double Magnitude = Math.sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                    double MagnitudeDelta = Magnitude - MagnitudePrevious;
                    MagnitudePrevious = Magnitude;

                    if (MagnitudeDelta > 5) {
                        stepCount++;
                    }
                    counter.setText(stepCount.toString());

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager.registerListener(stepDetector, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);


//        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)!=null){
//            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//            isCounterSensorPresent = true;
//        }
//        else{
//            counter.setText("Counter sensor is not present");
//            isCounterSensorPresent = false;
//        }




        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        greet =findViewById(R.id.greet);
        feed = findViewById(R.id.feed);
        notification =findViewById(R.id.notfication);

        feed.setImageResource(R.drawable.feed);
        notification.setImageResource(R.drawable.notification);




        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        /*signOut = findViewById(R.id.signOut);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();

            }
        });

    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        // ...
                    }
                });*/
    }
    protected void onPause(){
        super.onPause();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount",stepCount);
        editor.apply();
    }
    protected  void  onStop(){
        super.onStop();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("stepCount",stepCount);
        editor.apply();

    }
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        stepCount = sharedPreferences.getInt("stepCount",0);
    }



    private void gotoMainActivity(){
        startActivity(new Intent(homePage.this,register_user.class));
        finish();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
    }

    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            name.setText(account.getGivenName());
            Picasso.get().load(account.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(profile);
        }
        else{
            gotoMainActivity();

        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
        else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    handleSignInResult(result);
                }
            });
        }
    }

    public void openActivity(View view){
        Intent intent = new Intent(this, profile.class);
        startActivity(intent);
    }
    public void openActivity2(View view){
        Intent intent = new Intent(this, feed.class);
        startActivity(intent);
    }
    public void openActivity3(View view){
        Intent intent = new Intent(this, notification.class);
        startActivity(intent);
    }

//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//        if(sensorEvent.sensor == mStepCounter){
//            stepCount = (int) sensorEvent.values[0];
//            counter.setText(String.valueOf(stepCount));
//            }
//        }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }
//    @Override
//    protected  void onResume() {
//        super.onResume();
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
//            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//    @Override
//    protected void onPause(){
//        super.onPause();
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
//            sensorManager.unregisterListener(this,mStepCounter);
//
//        }

}