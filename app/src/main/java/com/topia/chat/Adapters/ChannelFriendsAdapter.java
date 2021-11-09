package com.topia.chat.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.topia.chat.Helper.ServerHelper;
import com.topia.chat.Models.RequestSenderModel;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ChannelFriendsAdapter extends RecyclerView.Adapter<ChannelFriendsAdapter.MyViewHolder>{
    ArrayList<RequestSenderModel> mlist;
    Context context;
    String serverID,serverName;


    DatabaseReference DBref;
    SessionManager sessionManager;
    HashMap<String,String> UserDetails;

    public ChannelFriendsAdapter(ArrayList<RequestSenderModel> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;


        sessionManager = new SessionManager(context);
        UserDetails = sessionManager.getUsersDetailsFromSessions();
        DBref = FirebaseDatabase.getInstance().getReference("users");
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    /*public String getChannelID() {
        //return ChannelID;
    }

    public void setChannelID(String channelID) {
        //ChannelID = channelID;
    }*/

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_channel_friends,parent,false);

        return new ChannelFriendsAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RequestSenderModel receiver = mlist.get(position);

        if(!receiver.getProfileImage().equals("No Image")){
            Glide.with(context).load(receiver.getProfileImage()).fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().into(holder.imageURL);
        }
        holder.userName.setText(receiver.getUserName());
        holder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HashMap<String,String> server_member_info = new HashMap<>();
                server_member_info.put("serverID",serverID);
                server_member_info.put("status","member");
                DBref.child(receiver.getUid()).child("server_invites").child(serverID).setValue(server_member_info).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Invite Sent", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageURL;
        TextView userName;
        Button invite;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageURL = itemView.findViewById(R.id.imageViewSender);
            userName = itemView.findViewById(R.id.inviteChannel);
            invite = itemView.findViewById(R.id.add_friend);

        }
    }
}