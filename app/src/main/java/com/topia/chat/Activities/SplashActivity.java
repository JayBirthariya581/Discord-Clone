package com.topia.chat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.topia.chat.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this, R.color.status2));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //startActivity(new Intent(SplashActivity.this,LoginActivity.class ));
                startActivity(new Intent(SplashActivity.this,ChatActivityDemo.class ));
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}