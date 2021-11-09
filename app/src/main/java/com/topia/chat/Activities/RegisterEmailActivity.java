package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.topia.chat.Models.User;
import com.topia.chat.Models.UserEmail;
import com.topia.chat.R;
import com.topia.chat.databinding.ActivityRegisterEmailBinding;
import com.topia.chat.databinding.ActivitySetupProfileBinding;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class RegisterEmailActivity extends AppCompatActivity {

    ImageView imageView;
    Button Register,AlreadySignIn;

    FirebaseStorage storage;
    TextInputLayout emailRegisterL, passwordRegisterL,UserNameRegisterL ;
    Uri selectedImage;
    ProgressDialog dialog;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);

        UserNameRegisterL = findViewById(R.id.UserNameRegisterL);
        emailRegisterL = findViewById(R.id.emailRegsiterL);
        passwordRegisterL = findViewById(R.id.passwordRegisterL);
        AlreadySignIn = findViewById(R.id.AlreadySignIn);
        imageView = findViewById(R.id.imageView);


        getWindow().setStatusBarColor(ContextCompat.getColor(RegisterEmailActivity.this,R.color.status2));


        reference = FirebaseDatabase.getInstance().getReference("users");
        auth = FirebaseAuth.getInstance();








        storage = FirebaseStorage.getInstance();

        Register = findViewById(R.id.Register);


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });


    }



    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }









    private Boolean validateEmail(){
        String email = emailRegisterL.getEditText().getText().toString();
        String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";


        if(email.isEmpty()){
            emailRegisterL.setError("Field cannot be empty");
            return false;
        }else if(!email.matches(emailPattern)){
            emailRegisterL.setError("Invalid Email");
            return false;
        }else{
            emailRegisterL.setError(null);
            emailRegisterL.setErrorEnabled(false);
            return true;
        }
    }



    public Boolean validatePassword(){
        String password = passwordRegisterL.getEditText().getText().toString();
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
            passwordRegisterL.setError("atleast 5 characters");
            return false;
        }else{
            passwordRegisterL.setError(null);
            return true;
        }

    }





    public void registerUser(){
        dialog.show();


        if( !validateEmail()  || !validatePassword()){
            return;
        }


        String email = emailRegisterL.getEditText().getText().toString();

        Query checkUserRegister = reference.orderByChild("email").equalTo(email);

        checkUserRegister.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterEmailActivity.this);
                    builder.setIcon(R.drawable.ic_main2);
                    builder.setTitle("Topia");

                    builder.setMessage("User with this email already Exists");
                    builder.show();
                }else{


                    if(selectedImage!=null){
                        StorageReference ref = storage.getReference().child("Profiles").child(email);

                        Bitmap bmp = null;
                        try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        //here you can choose quality factor in third parameter(ex. i choosen 25)
                        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byte[] fileInBytes = baos.toByteArray();


                        ref.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String userName = UserNameRegisterL.getEditText().getText().toString();
                                            String password = passwordRegisterL.getEditText().getText().toString();
                                            String emaill = emailRegisterL.getEditText().getText().toString();

                                            String imageUrl = uri.toString();





                                            DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("users");
                                            String UID = DBref.push().getKey();
                                            UserEmail userEmail = new UserEmail(UID,userName,emaill,imageUrl,password);

                                            DBref.child(UID).setValue(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        dialog.dismiss();
                                                        auth.createUserWithEmailAndPassword(emaill,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {


                                                                if(task.isSuccessful()){
                                                                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                dialog.dismiss();
                                                                                Toast.makeText(RegisterEmailActivity.this, "Verification Mail sent", Toast.LENGTH_LONG).show();
                                                                                startActivity(new Intent(RegisterEmailActivity.this,LoginActivity.class));
                                                                                finish();
                                                                            }else{
                                                                                Toast.makeText(RegisterEmailActivity.this, "oops", Toast.LENGTH_SHORT).show();
                                                                            }


                                                                        }
                                                                    });
                                                                }


                                                            }
                                                        });


                                                    }
                                                }
                                            });

                                        }
                                    });

                                }
                            }
                        });




                    }else{
                        String userName = UserNameRegisterL.getEditText().getText().toString();
                        String password = passwordRegisterL.getEditText().getText().toString();

                        DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("users");
                        String UID = DBref.push().getKey();

                        UserEmail userEmail = new UserEmail(UID,userName,email,"No Image",password);
                        DBref.child(UID).setValue(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {


                                            if(task.isSuccessful()){
                                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            dialog.dismiss();
                                                            Toast.makeText(RegisterEmailActivity.this, "Mail sent", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(RegisterEmailActivity.this,LoginActivity.class));
                                                            finish();
                                                        }else{
                                                            Toast.makeText(RegisterEmailActivity.this, "oops", Toast.LENGTH_SHORT).show();
                                                        }


                                                    }
                                                });
                                            }


                                        }
                                    });


                                }
                            }
                        });
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });















    }













    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {


                imageView.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }


}