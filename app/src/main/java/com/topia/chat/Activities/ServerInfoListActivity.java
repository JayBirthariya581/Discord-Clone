package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Adapters.FriendListAdapter;
import com.topia.chat.Adapters.ServerAdminAdapter;
import com.topia.chat.Adapters.ServerMemberAdapter;
import com.topia.chat.Models.RequestSenderModel;
import com.topia.chat.Models.UserTest;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerInfoListActivity extends AppCompatActivity {
    String list_type,serverID;
    RecyclerView recyclerView;

    TextView list_type_head,emt;
    SessionManager sessionManager;
    ArrayList<String> uids;
    ArrayList<UserTest> mlist;
    ProgressDialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;
    ServerAdminAdapter serverAdminAdapter;
    ServerMemberAdapter serverMemberAdapter;
    DatabaseReference SVref,DBref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_info_list);
        list_type = getIntent().getStringExtra("type");
        recyclerView = findViewById(R.id.rv_ListSvSetList);
        list_type_head = findViewById(R.id.server_name);
        mlist = new ArrayList<>();
        uids = new ArrayList<>();
        getWindow().setStatusBarColor(ContextCompat.getColor(ServerInfoListActivity.this,R.color.status2));

        dialog = new ProgressDialog(ServerInfoListActivity.this);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        serverID = getIntent().getStringExtra("serverID");
        DBref = FirebaseDatabase.getInstance().getReference("users");
        SVref = FirebaseDatabase.getInstance().getReference("servers").child(getIntent().getStringExtra("serverID"));
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        emt = findViewById(R.id.emText);
        sessionManager = new SessionManager(ServerInfoListActivity.this);
        emt.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ServerInfoListActivity.this));



        if(list_type.equals("admin")){
            list_type_head.setText("Admins");
            serverAdminAdapter = new ServerAdminAdapter(ServerInfoListActivity.this,mlist);
            serverAdminAdapter.setServerID(serverID);
            recyclerView.setAdapter(serverAdminAdapter);
            makelist();
        }

        if(list_type.equals("member")){
            list_type_head.setText("Members");
            serverMemberAdapter = new ServerMemberAdapter(ServerInfoListActivity.this,mlist);
            serverMemberAdapter.setServerID(serverID);
            recyclerView.setAdapter(serverMemberAdapter);
            makelist();
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makelist();
                swipeRefreshLayout.setRefreshing(false);
            }
        });



    }

    private void makelist() {
        dialog.show();
        emt.setVisibility(View.GONE);
        uids.clear();
        mlist.clear();
        if(list_type.equals("member")){
            SVref.child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot memberUIDs) {

                    if(memberUIDs.exists()){
                        for(DataSnapshot uid : memberUIDs.getChildren()){
                            uids.add(uid.getValue(String.class));
                        }


                        if(!uids.isEmpty()) {
                            for(int i=0;i<uids.size();i++){

                                if (!uids.get(i).equals(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))) {
                                    DBref.child(uids.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserTest m = snapshot.getValue(UserTest.class);

                                            mlist.add(m);
                                            serverMemberAdapter.notifyDataSetChanged();
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            }

                            dialog.dismiss();
                        }
                    }else{
                        dialog.dismiss();
                        emt.setVisibility(View.VISIBLE);
                    }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(list_type.equals("admin")){
            SVref.child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot memberUIDs) {

                    if(memberUIDs.exists()){
                        for(DataSnapshot uid : memberUIDs.getChildren()){
                            uids.add(uid.getValue(String.class));
                        }



                        for(int i=0;i<uids.size();i++){


                            DBref.child(uids.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserTest a = snapshot.getValue(UserTest.class);

                                    //if(!a.getUid().equals(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))){
                                    mlist.add(a);
                                    serverAdminAdapter.notifyDataSetChanged();
                                    //}



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        dialog.dismiss();
                    }else{
                        dialog.dismiss();
                        emt.setVisibility(View.VISIBLE);
                    }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }


}