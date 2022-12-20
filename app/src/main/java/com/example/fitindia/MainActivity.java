package com.example.fitindia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent i=new Intent(MainActivity.this,
                            register_user.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    //invoke the SecondActivity.

                    finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}