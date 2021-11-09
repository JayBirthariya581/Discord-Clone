package com.topia.chat.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Activities.CreateServerActivity;
import com.topia.chat.Adapters.ChannelListAdapter;
import com.topia.chat.Adapters.ServerListAdapter;
import com.topia.chat.Helper.ServerInfoDisplay;
import com.topia.chat.Models.ModelChannelList;
import com.topia.chat.Models.ModelServerList;
import com.topia.chat.Models.ModelUserServer;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ServerListFragment extends Fragment {
    RecyclerView recyclerView,recyclerView_channelList;
    Button InviteMembers,createChannel;
    TextView serverName;
    ImageView serverSettings,svDP1;
    DatabaseReference DBr,serverRef ;
    ProgressDialog dialog;
    SessionManager sessionManager;
    ServerListAdapter adapter;
    ChannelListAdapter  adapterC;
    ServerInfoDisplay serverInfoDisplay;
    ModelServerList modelServerList;
    MaterialCardView im,im1;
    ArrayList<ModelChannelList> mlistC;
    ArrayList<ModelUserServer> mlist;
    ArrayList<ModelServerList> serverList;
    SwipeRefreshLayout swr;
    LinearLayout intro;
    private View view;
    CardView fab,status;
    int i=0;
    ImageView serverDp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            view =inflater.inflate(R.layout.fragment_server_list, container, false);
            return view;

    }



    @Override
    public void onViewCreated(@NonNull @NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading Servers...");
        dialog.setCancelable(false);

        //server list
        serverList = new ArrayList<>();
        mlist = new ArrayList<>();

        //channel list
        mlistC= new ArrayList<>();

        //hooks
        swr = view.findViewById(R.id.srl);

        intro = view.findViewById(R.id.intro);

        //server hooks
        serverDp = view.findViewById(R.id.svDP);
        recyclerView = view.findViewById(R.id.rv_server_list);
        fab = view.findViewById(R.id.fab);
        status = view.findViewById(R.id.status);
        im = view.findViewById(R.id.inviteMarker);
        im1 = view.findViewById(R.id.requestMarker);
        svDP1 = view.findViewById(R.id.svDP1);
        //Channel Hooks
        serverName = view.findViewById(R.id.Server_Name);
        InviteMembers = view.findViewById(R.id.inviteMembers);
        serverSettings = view.findViewById(R.id.server_settings);
        createChannel = view.findViewById(R.id.CreateChannel);
        recyclerView_channelList = view.findViewById(R.id.recyclerView_cl);

        //Variables
        sessionManager = new SessionManager(getContext());
        DBr = FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions()
                .get(SessionManager.KEY_UID)).child("servers");

        serverRef = FirebaseDatabase.getInstance().getReference("servers");


        //server recycler view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Channel recycler view
        recyclerView_channelList.setHasFixedSize(true);
        recyclerView_channelList.setLayoutManager(new LinearLayoutManager(getContext()));


        //channel recyclerview + adapter
        adapterC = new ChannelListAdapter(mlistC,getContext());
        adapterC.setServerOwner(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        recyclerView_channelList.setAdapter(adapterC);


        //ServerInfoDisplay Initialisation
        serverInfoDisplay = new ServerInfoDisplay(getContext(),serverName,createChannel,InviteMembers,mlistC,adapterC,serverSettings,serverDp);

        //server recyclerview + adapter
        adapter = new ServerListAdapter(serverInfoDisplay,serverList,getContext());
        recyclerView.setAdapter(adapter);




        makeList();


        swr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeList();
                swr.setRefreshing(false);
            }
        });


        FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions()
                .get(SessionManager.KEY_UID)).child("Friends").child("FriendRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.getChildrenCount()>0){
                        im1.setVisibility(View.VISIBLE);


                    }else {
                        im1.setVisibility(View.GONE);
                    }
                }else{
                    im1.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions()
                .get(SessionManager.KEY_UID)).child("server_invites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.getChildrenCount()>0){
                        im.setVisibility(View.VISIBLE);


                    }else {
                        im.setVisibility(View.GONE);
                    }
                }else{
                    im.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CreateServerActivity.class));
                //Toast.makeText(getContext(), sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_USERNAME), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void makeList(){
        if(isOnline()){
            updateList();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setIcon(R.drawable.main12);
            builder.setTitle("uTopia");
            builder.setMessage("You are offline");
            builder.setCancelable(false);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(isOnline()){
                        makeList();
                    }else{
                        builder.show();
                    }
                }
            });
            builder.show();

        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            //Toast.makeText(getContext(), "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void updateList() {
        dialog.show();
        //* progressBar.setVisibility(View.VISIBLE);
        mlist.clear();
        serverList.clear();



        DBr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ModelUserServer modelUserServer = dataSnapshot.getValue(ModelUserServer.class);
                        mlist.add(modelUserServer);
                    }

                    for( i = 0;i < mlist.size();i++){
                        serverRef.child(mlist.get(i).getServerID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                modelServerList = snapshot.getValue(ModelServerList.class);
                                modelServerList.setServerUid(snapshot.getKey());





                                serverList.add(modelServerList);
                                adapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    dialog.dismiss();
                }else{

                    dialog.dismiss();
                    svDP1.setVisibility(View.GONE);
                    serverName.setVisibility(View.GONE);
                    serverDp.setVisibility(View.GONE);
                    createChannel.setVisibility(View.GONE);
                    InviteMembers.setVisibility(View.GONE);
                    serverSettings.setVisibility(View.GONE);
                    recyclerView_channelList.setVisibility(View.GONE);
                    intro.setVisibility(View.VISIBLE);
                }


            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }





}