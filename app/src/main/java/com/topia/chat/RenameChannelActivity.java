package com.topia.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.topia.chat.Activities.MainContainerActivity;

public class RenameChannelActivity extends AppCompatActivity {
    TextInputLayout renameChannelL;
    Button renameChannel;

    DatabaseReference serverRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename_channel);
        renameChannelL = findViewById(R.id.ChannelRenameNameRL);
        renameChannel = findViewById(R.id.RenameChannel);
        serverRef = FirebaseDatabase.getInstance().getReference("servers");


        renameChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(renameChannelL.getEditText().getText().toString().equals("")){
                    Toast.makeText(RenameChannelActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }

                else{
                    serverRef.child(getIntent().getStringExtra("serverID")).child("channels").child(getIntent().getStringExtra("channelID"))
                            .child("channelName").setValue(renameChannelL.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(RenameChannelActivity.this, "Channel renamed successfully", Toast.LENGTH_SHORT).show();
                           finish();
                        }
                    });
                }




            }
        });






    }
}