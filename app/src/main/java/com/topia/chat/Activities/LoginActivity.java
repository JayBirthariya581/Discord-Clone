package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.topia.chat.Models.User;
import com.topia.chat.Models.UserTest;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
TextInputLayout emailL,passwordL;
Button signIn,newUser,forgotpassword;
ProgressBar progressBar;

FirebaseAuth auth;
UserTest user;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


       sessionManager = new SessionManager(LoginActivity.this);
        auth = FirebaseAuth.getInstance();
        //Hooks
        emailL = findViewById(R.id.emailLoginL);
        passwordL = findViewById(R.id.passwordLoginL);
        signIn = findViewById(R.id.Login);
        progressBar = findViewById(R.id.progress_barL);
        newUser = findViewById(R.id.Register);
        forgotpassword = findViewById(R.id.forgotPassword);
        progressBar.setVisibility(View.GONE);
        getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this,R.color.status2));

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(signIn.getWindowToken(), 0);
                        signInUser();

            }
        });


        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterEmailActivity.class));
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });






    }

    private void signInUser() {
        progressBar.setVisibility(View.VISIBLE);


        if( !validateEmail()  || !validatePassword()){
            return;
        }

        DatabaseReference DBref =  FirebaseDatabase.getInstance().getReference("users");



        Query checkUser = DBref.orderByChild("email").equalTo(emailL.getEditText().getText().toString());


        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    emailL.setError(null);
                    passwordL.setErrorEnabled(false);


                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        user = dataSnapshot.getValue(UserTest.class);
                        user.setUid(dataSnapshot.getKey());

                    }








                            String passwordFromDB = user.getPassword();




                            String userNameFromDB = user.getUserName();
                            String ImageFromDB = user.getProfileImage();

                            String emailFromDB = user.getEmail();

                            auth.signInWithEmailAndPassword(emailL.getEditText().getText().toString(),passwordL.getEditText().getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                            progressBar.setVisibility(View.GONE);

                                            if(task.isSuccessful()){

                                                if(!passwordFromDB.equals(passwordL.getEditText().getText().toString())){
                                                    FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("password").setValue(passwordL.getEditText().getText().toString());
                                                }


                                                if(auth.getCurrentUser().isEmailVerified()){


                                                    FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                                                        @Override
                                                        public void onSuccess(String tok) {
                                                            FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("token").setValue(tok);
                                                            sessionManager.createLoginSession(user.getUid(),user.getUserName(),ImageFromDB,emailFromDB,passwordFromDB,tok);

                                                            startActivity(new Intent(LoginActivity.this,MainContainerActivity.class));
                                                            finish();

                                                        }
                                                    });


                                                }else{
                                                    Toast.makeText(LoginActivity.this, "Please Verify your email address", Toast.LENGTH_SHORT).show();
                                                }


                                            }else{


                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });












                }else{
                    progressBar.setVisibility(View.GONE);
                    emailL.setError("No such user exist");
                    emailL.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });




    }


    private Boolean validateEmail(){
        String email = emailL.getEditText().getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";


        if(email.isEmpty()){
            emailL.setError("Field cannot be empty");
            return false;
        }else if(!email.matches(emailPattern)){
            emailL.setError("Invalid Email");
            return false;
        }else{
            emailL.setError(null);
            emailL.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() && sessionManager.checkLogin()){
                startActivity(new Intent(LoginActivity.this,MainContainerActivity.class));
                finish();
            }

        }
    }


    public Boolean validatePassword(){
        String password = passwordL.getEditText().getText().toString();
        String passwordMatch ="^"+
                "(?=.*[0-9])"+
                "(?=.*[a-z])"+
                "(?=.*[A-Z])"+
                "(?=.*[@#$%^&+=])"+
                "(?=\\S+$)"+
                ".{4,}"+
                "$";



       /* if(password.isEmpty()){
            passwordRegisterL.setError("Field cannot be empty");
            return false;
        }else */
        if(password.length()<5){
            passwordL.setError("atleast 5 characters");
            return false;
        }else{
            passwordL.setError(null);
            return true;
        }

    }

}