package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.topia.chat.Helper.ChannelHelper;
import com.topia.chat.Helper.ServerHelper;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import org.jetbrains.annotations.NotNull;

public class CreateChannelActivity extends AppCompatActivity {
    DatabaseReference DBref,serverRef;
    TextInputLayout channelName;
    SessionManager sessionManager;
    Button createChannel;
    String serverID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        channelName = findViewById(R.id.ChannelNameRL);
        createChannel = findViewById(R.id.CreateChannel);
        serverID = getIntent().getStringExtra("server_ID");

        getWindow().setStatusBarColor(ContextCompat.getColor(CreateChannelActivity.this,R.color.status2));
        sessionManager = new SessionManager(CreateChannelActivity.this);
        DBref = FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions()
                .get(SessionManager.KEY_UID)).child("servers").child(serverID).child("channels");
        serverRef = FirebaseDatabase.getInstance().getReference("servers").child(serverID).child("channels");
        createChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(createChannel.getWindowToken(), 0);
                createChannel();
            }
        });
    }

    private void createChannel() {
        if(channelName.getEditText().getText().toString().equals("")){
            channelName.setError("Field cannot be empty");
            return;
        }


        Query checkChannel = serverRef.orderByChild("channelName").equalTo(channelName.getEditText().getText().toString());


        checkChannel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(CreateChannelActivity.this, "Channel with this name already exists", Toast.LENGTH_SHORT).show();
                }else{

                    ChannelHelper channelHelper = new ChannelHelper(channelName.getEditText().getText().toString(),"1");

                    serverRef.push().setValue(channelHelper).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CreateChannelActivity.this, "Channel Created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateChannelActivity.this,MainContainerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
}