package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.topia.chat.Models.UserEmail;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {
    ImageView imageView;
    Button update;
    SessionManager sessionManager;
    FirebaseStorage storage;
    TextInputLayout UserNameUpdateL ;
    Uri selectedImage;
        ProgressDialog dialog;
        FirebaseAuth auth;
        DatabaseReference reference;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_update_profile);
            getWindow().setStatusBarColor(ContextCompat.getColor(UpdateProfileActivity.this,R.color.status2));

            dialog = new ProgressDialog(this);
            dialog.setMessage("Updating profile...");
            dialog.setCancelable(false);

            UserNameUpdateL = findViewById(R.id.UserNameUpdateL);
            //update = findViewById(R.id.updateProfile);


            imageView = findViewById(R.id.imageView);


            sessionManager = new SessionManager(UpdateProfileActivity.this);

            reference = FirebaseDatabase.getInstance().getReference("users");
            auth = FirebaseAuth.getInstance();


            UserNameUpdateL.getEditText().setText(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_USERNAME));





            storage = FirebaseStorage.getInstance();

            update = findViewById(R.id.Update);


            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(update.getWindowToken(), 0);
                    updateUser();
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

















        public void updateUser(){
            dialog.show();


            if(UserNameUpdateL.getEditText().getText().toString().equals("")){
               dialog.dismiss();
               UserNameUpdateL.setError("Field cannot be empty");
               return;
            }




            Query checkUserRegister = reference.orderByChild("email").equalTo(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL));

            checkUserRegister.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        if(selectedImage!=null){

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


                            StorageReference ref = storage.getReference().child("Profiles").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL));
                            ref.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String userName = UserNameUpdateL.getEditText().getText().toString();


                                                String imageUrl = uri.toString();


                                                DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("users")
                                                        .child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));






                                                DBref.child("userName").setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){

                                                            DBref.child("profileImage").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sessionManager.getEditor().putString(SessionManager.KEY_PROFILE_IMAGE,imageUrl);
                                                                    sessionManager.getEditor().putString(SessionManager.KEY_USERNAME,userName);
                                                                    sessionManager.getEditor().commit();

                                                                    Intent intent = new Intent(UpdateProfileActivity.this,MainContainerActivity.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    startActivity(intent);
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
                            String userName = UserNameUpdateL.getEditText().getText().toString();




                            DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("users")
                                    .child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));







                            DBref.child("userName").setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    DBref.child("profileImage").setValue("No Image").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            sessionManager.getEditor().putString(SessionManager.KEY_PROFILE_IMAGE,"No Image");
                                            sessionManager.getEditor().putString(SessionManager.KEY_USERNAME,userName);
                                            sessionManager.getEditor().commit();

                                            Intent intent = new Intent(UpdateProfileActivity.this,MainContainerActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    });
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