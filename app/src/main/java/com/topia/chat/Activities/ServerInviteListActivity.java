package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Adapters.ServerInviteAdapter;
import com.topia.chat.Models.ModelServerList;
import com.topia.chat.Models.ModelUserServer;
import com.topia.chat.Models.RequestSenderModel;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;

public class ServerInviteListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ModelServerList> mlist;
    ArrayList<ModelUserServer> ilist;
    DatabaseReference DBref,SVref;
    SessionManager sessionManager;
    TextView emt;
    int i;
    ServerInviteAdapter serverInviteAdapter;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_invite_list);
        recyclerView = findViewById(R.id.rv_sv_invite);
        getWindow().setStatusBarColor(ContextCompat.getColor(ServerInviteListActivity.this,R.color.black));
        emt = findViewById(R.id.emText);
        emt.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setHasFixedSize(true);
        sessionManager = new SessionManager(ServerInviteListActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ServerInviteListActivity.this));
        DBref = FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        SVref = FirebaseDatabase.getInstance().getReference("servers");


        mlist = new ArrayList<>();
        ilist = new ArrayList<>();
        serverInviteAdapter = new ServerInviteAdapter(ServerInviteListActivity.this,mlist);

        recyclerView.setAdapter(serverInviteAdapter);


        makeList();






    }

    private void makeList() {
        emt.setVisibility(View.GONE);
        ilist.clear();
        mlist.clear();
        DBref.child("server_invites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot inviteList) {

                if(inviteList.exists()){
                    for(DataSnapshot singleInvite : inviteList.getChildren()){

                        ilist.add(singleInvite.getValue(ModelUserServer.class));
                    }


                    for(i=0;i<ilist.size();i++){

                        SVref.child(ilist.get(i).getServerID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.exists()){
                                    ModelServerList m = snapshot.getValue(ModelServerList.class);
                                    m.setServerUid(snapshot.getKey());


                                    mlist.add(m);

                                    serverInviteAdapter.notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    }
                }else{
                    emt.setVisibility(View.VISIBLE);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}