package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Helper.FriendRequestHelper;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.HashMap;

public class ServerInviteMemberActivity extends AppCompatActivity {
    TextInputLayout userName;
    Button Invite;
    DatabaseReference DBref;
    ProgressDialog dialog;
    SessionManager sessionManager;
    HashMap<String,String> UserDetails;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_invite_member);
        userName = findViewById(R.id.SearchUserName);
        Invite = findViewById(R.id.Invite);
        getWindow().setStatusBarColor(ContextCompat.getColor(ServerInviteMemberActivity.this,R.color.black));

        //Variables
        sessionManager = new SessionManager(ServerInviteMemberActivity.this);
        UserDetails = sessionManager.getUsersDetailsFromSessions();
        DBref = FirebaseDatabase.getInstance().getReference("users");

        dialog = new ProgressDialog(ServerInviteMemberActivity.this);
        dialog.setMessage("Processing Request...");
        dialog.setCancelable(false);

        Invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                processRequest();

            }
        });







    }

    private void processRequest() {
        dialog.show();
        if(userName.getEditText().getText().toString().equals(UserDetails.get(SessionManager.KEY_USERNAME)) ){
            userName.setError("Invalid UserName");
            dialog.dismiss();
            return;
        }

        if(userName.getEditText().getText().toString().equals("")){
            userName.setError("Field cannot be empty");
            dialog.dismiss();
            return;
        }

        Query checkuser = DBref.orderByChild("userName").equalTo(userName.getEditText().getText().toString());

        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String receiverUID="";
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        receiverUID = dataSnapshot.getKey();
                    }

                    HashMap<String,String> serverInvite = new HashMap<>();
                    //serverInvite.put("serverName",)
                    FriendRequestHelper request = new FriendRequestHelper(UserDetails.get(SessionManager.KEY_UID),receiverUID);

                    Query checkFriends = DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendList").orderByValue().equalTo(request.getReceiverUID());

                    checkFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                dialog.dismiss();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ServerInviteMemberActivity.this);
                                alertDialog.setMessage("Already Friends");
                                alertDialog.setCancelable(true);
                                alertDialog.setIcon(R.drawable.ic_main2);
                                alertDialog.setTitle("Topia");
                                alertDialog.show();
                            }else{
                                DBref.child(request.getReceiverUID()).child("Friends").child("FriendRequests").child(UserDetails.get(SessionManager.KEY_UID)).setValue(request)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    dialog.dismiss();
                                                    userName.setError(null);
                                                    userName.getEditText().setText("");
                                                    Toast.makeText(ServerInviteMemberActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                }else{
                    dialog.dismiss();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ServerInviteMemberActivity.this);
                    alertDialog.setMessage("No such user exist");
                    alertDialog.setCancelable(true);
                    alertDialog.setIcon(R.drawable.ic_main2);
                    alertDialog.setTitle("Topia");
                    alertDialog.show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}