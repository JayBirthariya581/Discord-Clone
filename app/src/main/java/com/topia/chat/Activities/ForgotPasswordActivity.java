package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputLayout emailL;
    Button sendLink;
    DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailL = findViewById(R.id.emailFPL);
        sendLink = findViewById(R.id.sendLink);
        dbref = FirebaseDatabase.getInstance().getReference("users");

        getWindow().setStatusBarColor(ContextCompat.getColor(ForgotPasswordActivity.this,R.color.status2));


        sendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendLink.getWindowToken(), 0);
                processForgotPassword();
            }
        });




    }

    private void processForgotPassword() {

    if(emailL.getEditText().getText().toString().isEmpty()){
        emailL.setError("Invalid Email");
        return;
    }

        Query checkUser = dbref.orderByChild("email").equalTo(emailL.getEditText().getText().toString());


        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailL.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                emailL.setError(null);
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }else{
                    emailL.setError("No such user exists");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}