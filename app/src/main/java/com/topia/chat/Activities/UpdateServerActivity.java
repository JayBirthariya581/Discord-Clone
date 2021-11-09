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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateServerActivity extends AppCompatActivity {
    CircleImageView serverDp;
    TextInputLayout serverNameL;
    Button updateServer;
    SessionManager sessionManager;
    FirebaseStorage storage;
    String serverId,svName,serverImage;
    Uri selectedImage;
    ProgressDialog dialog;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_server);
        serverDp = findViewById(R.id.imageView);
        serverNameL = findViewById(R.id.ServerNameUpdateL);
        updateServer = findViewById(R.id.Update);

        getWindow().setStatusBarColor(ContextCompat.getColor(UpdateServerActivity.this,R.color.status2));

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating server...");
        dialog.setCancelable(false);


        //update = findViewById(R.id.updateProfile);

        serverId = getIntent().getStringExtra("serverID");
        svName = getIntent().getStringExtra("serverName");
        serverImage = getIntent().getStringExtra("serverImage");



        sessionManager = new SessionManager(UpdateServerActivity.this);

        reference = FirebaseDatabase.getInstance().getReference("servers");



        serverNameL.getEditText().setText(svName);


        storage = FirebaseStorage.getInstance();


        updateServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(updateServer.getWindowToken(), 0);
                updateServer();
            }
        });

        serverDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });


    }





















    public void updateServer(){
        dialog.show();


        if(serverNameL.getEditText().getText().toString().equals("")){
            dialog.dismiss();
            serverNameL.setError("Field cannot be empty");
            return;
        }






        reference.child(serverId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            String serverN = serverNameL.getEditText().getText().toString();


                                            String imageUrl = uri.toString();









                                            reference.child(serverId).child("serverName").setValue(serverN).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){

                                                        reference.child(serverId).child("serverImage").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                Intent intent = new Intent(UpdateServerActivity.this, ServerSettingActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                intent.putExtra("serverID",serverId);
                                                                intent.putExtra("serverName",serverN);
                                                                intent.putExtra("serverImage",imageUrl);

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
                        String serverN = serverNameL.getEditText().getText().toString();




                        DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("users")
                                .child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));







                        reference.child(serverId).child("serverName").setValue(serverN).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                reference.child(serverId).child("serverImage").setValue("No Image");
                                Intent intent = new Intent(UpdateServerActivity.this, ServerSettingActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("serverID",serverId);
                                intent.putExtra("serverName",serverN);
                                intent.putExtra("serverImage","No Image");

                                startActivity(intent);
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


                serverDp.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }


}