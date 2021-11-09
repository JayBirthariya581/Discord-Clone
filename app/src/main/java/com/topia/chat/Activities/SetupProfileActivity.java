package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.topia.chat.Models.User;
import com.topia.chat.R;
import com.topia.chat.databinding.ActivitySetupProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    Button Register,AlreadySignIn;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    TextInputLayout LastNameRegisterLayout,emailRegisterL, passwordRegisterL,phoneRegisterL,FirstNameRegisterL ;
    Uri selectedImage;
    ProgressDialog dialog;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);


        emailRegisterL = findViewById(R.id.emailRegsiterL);
        passwordRegisterL = findViewById(R.id.passwordRegisterL);
        AlreadySignIn = findViewById(R.id.AlreadySignIn);
        LastNameRegisterLayout = findViewById(R.id.LastNameRegisterLayout);
        /*phoneRegisterL = findViewById(R.id.phoneRegisterL);*/
        FirstNameRegisterL = findViewById(R.id.FirstNameRegisterL);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        Register = findViewById(R.id.Register);
        getSupportActionBar().hide();

       /* Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });*/

        binding.imageView.setOnClickListener(new View.OnClickListener() {
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


    //Methods to VALIDATE CREDENTIALS

    public void validateCredentials(){
        validateName();
        validateLastName();
        validateEmail();
        validatePhone();
        validatePassword();
    }


    private Boolean validateName(){
        String fullname = FirstNameRegisterL.getEditText().getText().toString();


        if(fullname.isEmpty()){
            FirstNameRegisterL.setError("Field cannot be empty");
            return false;
        }else{
            FirstNameRegisterL.setError(null);
            FirstNameRegisterL.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateLastName(){
        String lastName = LastNameRegisterLayout.getEditText().getText().toString();


        if(lastName.isEmpty()){
            LastNameRegisterLayout.setError("Field cannot be empty");
            return false;
        }else{
            LastNameRegisterLayout.setError(null);
            LastNameRegisterLayout.setErrorEnabled(false);
            return true;
        }

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

    private Boolean validatePhone(){
        String phone = phoneRegisterL.getEditText().getText().toString();

        if(phone.isEmpty()) {
            phoneRegisterL.setError("Field cannot be empty");
            return false;
        }else{
            phoneRegisterL.setError(null);
            phoneRegisterL.setErrorEnabled(false);
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





   /* public void registerUser(){
        FirebaseAuth.getInstance().signOut();


        if(!validateName() || !validateLastName() || !validateEmail() || !validatePhone() || !validatePassword()){
            return;
        }

        if(selectedImage != null) {
            StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    rootNode = FirebaseDatabase.getInstance();

                                    String imageUrl = uri.toString();
                                    String uid = auth.getUid();

                                    String firstName = FirstNameRegisterL.getEditText().getText().toString();
                                    String lastname = LastNameRegisterLayout.getEditText().getText().toString();
                                    String email = emailRegisterL.getEditText().getText().toString();
                                    String phoneNo = getIntent().getStringExtra("phone");
                                    String password = passwordRegisterL.getEditText().getText().toString();


                                    User user = new User(uid,firstName, lastname, email, phoneNo, imageUrl, password);


                                    database.getReference()
                                            .child("users")
                                            .child(uid)
                                            .setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });

                                    Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }

                    }
                });
            }else {
                String uid = auth.getUid();



                String firstName = FirstNameRegisterL.getEditText().getText().toString();
                String lastname = LastNameRegisterLayout.getEditText().getText().toString();
                String email = emailRegisterL.getEditText().getText().toString();
                String phoneNo = getIntent().getStringExtra("phone");
                String password = passwordRegisterL.getEditText().getText().toString();

                User user = new User(uid,firstName, lastname, email, phoneNo, "No Image", password);

                database.getReference()
                        .child("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }





    }
*/












    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if(data.getData() != null) {
                Uri uri = data.getData(); // filepath
                FirebaseStorage storage = FirebaseStorage.getInstance();
                long time = new Date().getTime();
                StorageReference reference = storage.getReference().child("Profiles").child(time+"");



                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //here you can choose quality factor in third parameter(ex. i choosen 25)
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] fileInBytes = baos.toByteArray();




                reference.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("image", filePath);
                                    database.getReference().child("users")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                }
                            });
                        }
                    }
                });


                binding.imageView.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}