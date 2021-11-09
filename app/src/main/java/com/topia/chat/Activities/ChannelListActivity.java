package com.topia.chat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.topia.chat.Adapters.ChannelListAdapter;
import com.topia.chat.Models.ModelChannelList;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;

public class ChannelListActivity extends AppCompatActivity {
    String serverID,serverOwner;

    RecyclerView recyclerView;
    LinearLayout upper;
    DatabaseReference DBr ;
    ProgressDialog dialog;
    SessionManager sessionManager;
    ChannelListAdapter adapter;
    SwipeRefreshLayout mySwipeRefreshLayout;
    //PresenceViewModel serverListViewModel;
    ArrayList<ModelChannelList> mlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);

        upper = findViewById(R.id.upper_channel);
        mySwipeRefreshLayout=findViewById(R.id.swiperefresh_CL);
        recyclerView = findViewById(R.id.rv_channel_list);
        serverID = getIntent().getStringExtra("server_ID");
        serverOwner = getIntent().getStringExtra("server_Owner");

        dialog = new ProgressDialog(ChannelListActivity.this);
        dialog.setMessage("Loading Servers...");
        dialog.setCancelable(false);




        sessionManager = new SessionManager(ChannelListActivity.this);
        mlist = new ArrayList<>();
        adapter = new ChannelListAdapter(mlist,ChannelListActivity.this);
        adapter.setServerID(serverID);
        adapter.setServerOwner(serverOwner);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChannelListActivity.this));

        recyclerView.setAdapter(adapter);


        upper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChannelListActivity.this, CreateChannelActivity.class);
                intent.putExtra("server_ID",serverID);
                startActivity(intent);

            }
        });



        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );



    }


}