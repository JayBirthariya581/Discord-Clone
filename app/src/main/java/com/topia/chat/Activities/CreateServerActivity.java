package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.topia.chat.Helper.ServerHelper;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class CreateServerActivity extends AppCompatActivity {
    DatabaseReference DBref,serverRef;
    TextInputLayout serverName;
    ImageView imageView,serverInvites;
    Uri selectedImage;
    SessionManager sessionManager;
    Button createServer;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_server);
        serverName = findViewById(R.id.ServerNameRegisterL);
        createServer = findViewById(R.id.CreateServer);
        imageView = findViewById(R.id.imageViewServer);

        serverInvites = findViewById(R.id.server_Invites);
        dialog = new ProgressDialog(CreateServerActivity.this);
        dialog.setMessage("Creating Server...");
        dialog.setCancelable(false);

        getWindow().setStatusBarColor(ContextCompat.getColor(CreateServerActivity.this,R.color.status2));
        sessionManager = new SessionManager(CreateServerActivity.this);
        DBref = FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).child("servers");
        serverRef = FirebaseDatabase.getInstance().getReference("servers");
        createServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(createServer.getWindowToken(), 0);
                createServer();
            }
        });

        serverInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateServerActivity.this,ServerInviteListActivity.class);
                startActivity(intent);
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

    private void createServer() {

        if(serverName.getEditText().getText().toString().equals("")){
            serverName.setError("Field cannot be empty");
            return;
        }
        dialog.show();
        Query checkserver = DBref.orderByChild("serverName").equalTo(serverName.getEditText().getText().toString());


        checkserver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(CreateServerActivity.this, "Server with this name already exists", Toast.LENGTH_SHORT).show();
                }else{


                    if(selectedImage!=null){


                        //Uri selectedImageUri = selectedImage.getData();

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



                        StorageReference ref = FirebaseStorage.getInstance().getReference().child("Profiles")
                                .child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL))
                                .child(serverName.getEditText().getText().toString());






                        ref.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String SERVER_ID = serverRef.push().getKey();

                                            ServerHelper serverHelper = new ServerHelper(serverName.getEditText().getText().toString(),uri.toString(),sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
                                            serverRef.child(SERVER_ID).setValue(serverHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        serverRef.child(SERVER_ID).child("Admins").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).setValue(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));

                                                        HashMap<String,String> server_owner_info = new HashMap<>();
                                                        server_owner_info.put("serverID",SERVER_ID);
                                                        server_owner_info.put("status","admin");


                                                        DBref.child(SERVER_ID).setValue(server_owner_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    dialog.dismiss();
                                                                    Toast.makeText(CreateServerActivity.this, "Server Created", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(CreateServerActivity.this,MainContainerActivity.class));
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





                    }else {
                        String SERVER_ID = serverRef.push().getKey();
                        ServerHelper serverHelper = new ServerHelper(serverName.getEditText().getText().toString(), "No Image", sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
                        serverRef.child(SERVER_ID).setValue(serverHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    serverRef.child(SERVER_ID).child("Admins").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).setValue(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));

                                    HashMap<String,String> server_owner_info = new HashMap<>();
                                    server_owner_info.put("serverID",SERVER_ID);
                                    server_owner_info.put("status","owner");

                                    DBref.child(SERVER_ID).setValue(server_owner_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                dialog.dismiss();
                                                Toast.makeText(CreateServerActivity.this, "Server Created", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(CreateServerActivity.this,MainContainerActivity.class));
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