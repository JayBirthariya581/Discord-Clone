package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class SearchFriendActivity extends AppCompatActivity {
    TextInputLayout searchUserName;
    Button sendRequest;
    DatabaseReference DBref;
    ProgressDialog dialog;
    SessionManager sessionManager;
    HashMap<String,String> UserDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        getWindow().setStatusBarColor(ContextCompat.getColor(SearchFriendActivity.this,R.color.status));
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //Hooks
        searchUserName = findViewById(R.id.SearchUserName);
        sendRequest = findViewById(R.id.SendFriendRequest);

        //Variables
        sessionManager = new SessionManager(SearchFriendActivity.this);
        UserDetails = sessionManager.getUsersDetailsFromSessions();
        DBref = FirebaseDatabase.getInstance().getReference("users");

        dialog = new ProgressDialog(SearchFriendActivity.this);
        dialog.setMessage("Processing Request...");
        dialog.setCancelable(false);

        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendRequest.getWindowToken(), 0);
                processRequest();

            }
        });







    }

    private void processRequest() {
        dialog.show();
        if(searchUserName.getEditText().getText().toString().equals(UserDetails.get(SessionManager.KEY_USERNAME)) ){
            searchUserName.setError("Invalid UserName");
            dialog.dismiss();
            return;
        }

        if(searchUserName.getEditText().getText().toString().equals("")){
            searchUserName.setError("Field cannot be empty");
            dialog.dismiss();
            return;
        }

        Query checkuser = DBref.orderByChild("userName").equalTo(searchUserName.getEditText().getText().toString());

        checkuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String receiverUID="";
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        receiverUID = dataSnapshot.getKey();
                    }


                    FriendRequestHelper request = new FriendRequestHelper(UserDetails.get(SessionManager.KEY_UID),receiverUID);

                    Query checkFriends = DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendList").orderByValue().equalTo(request.getReceiverUID());

                    checkFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                dialog.dismiss();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchFriendActivity.this);
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
                                                    searchUserName.setError(null);
                                                    searchUserName.getEditText().setText("");
                                                    Toast.makeText(SearchFriendActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
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
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchFriendActivity.this);
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