package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Adapters.ChannelFriendsAdapter;
import com.topia.chat.Models.RequestSenderModel;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerInviteFriendsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<RequestSenderModel> requests;
    ChannelFriendsAdapter channelFriendsAdapter;
    ArrayList<String> friendList_uids,members_uids,admin_uids;
    DatabaseReference DBref,SVref;
    SwipeRefreshLayout mySwipeRefreshLayout;
    SessionManager sessionManager;

    ProgressBar progressBar;
    HashMap<String,String> UserDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_friends);

        getWindow().setStatusBarColor(ContextCompat.getColor(ServerInviteFriendsActivity.this,R.color.black));

        recyclerView =findViewById(R.id.rv_FriendList);
        requests = new ArrayList<>();

        members_uids = new ArrayList<>();
        friendList_uids= new ArrayList<>();
        admin_uids= new ArrayList<>();
        mySwipeRefreshLayout=findViewById(R.id.swiperefresh);
        channelFriendsAdapter = new ChannelFriendsAdapter(requests, ServerInviteFriendsActivity.this);
        channelFriendsAdapter.setServerID(getIntent().getStringExtra("serverID"));
        //channelFriendsAdapter.setChannelID(getIntent().getStringExtra("channelID"));
        //channelFriendsAdapter.setChannelID(getIntent().getStringExtra("channelID"));
        recyclerView.setLayoutManager(new LinearLayoutManager(ServerInviteFriendsActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(channelFriendsAdapter);
        progressBar=findViewById(R.id.progress_bar);

        //Variables
        sessionManager = new SessionManager(ServerInviteFriendsActivity.this);
        UserDetails = sessionManager.getUsersDetailsFromSessions();
        DBref = FirebaseDatabase.getInstance().getReference("users");
        SVref = FirebaseDatabase.getInstance().getReference("servers");

        progressBar.setVisibility(View.VISIBLE);

        makeList();

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        makeList();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void makeList(){

        friendList_uids.clear();
        requests.clear();
        DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendList")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot request_List) {

                        for(DataSnapshot single_Request : request_List.getChildren()){
                            friendList_uids.add(single_Request.getValue(String.class));

                        }

                        SVref.child(getIntent().getStringExtra("serverID")).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for(DataSnapshot member : snapshot.getChildren()){
                                    members_uids.add(member.getValue(String.class));
                                }

                                SVref.child(getIntent().getStringExtra("serverID")).child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot admins) {

                                        for(DataSnapshot admin : admins.getChildren()){
                                            admin_uids.add(admin.getValue(String.class));
                                        }



                                        for(int i=0;i<friendList_uids.size();i++){

                                            if(!members_uids.contains(friendList_uids.get(i))  &&  !admin_uids.contains(friendList_uids.get(i))) {
                                                DBref.child(friendList_uids.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot senderDetail) {
                                                        RequestSenderModel Details = senderDetail.getValue(RequestSenderModel.class);

                                                        requests.add(Details);
                                                        channelFriendsAdapter.notifyDataSetChanged();

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                            }

                                        }







                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });









                                if(channelFriendsAdapter.getItemCount()==0){

                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });






                        progressBar.setVisibility(View.GONE);






                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}