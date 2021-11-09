package com.topia.chat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

public class GettingStartedActivity extends AppCompatActivity {
    Button gettingStrarted,signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);




        gettingStrarted = findViewById(R.id.GetStarted);
        signUp = findViewById(R.id.Register);



        gettingStrarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GettingStartedActivity.this,LoginActivity.class));

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GettingStartedActivity.this,RegisterEmailActivity.class));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() && new SessionManager(GettingStartedActivity.this).checkLogin()){
            startActivity(new Intent(GettingStartedActivity.this,MainContainerActivity.class));
            finish();
        }
    }
}