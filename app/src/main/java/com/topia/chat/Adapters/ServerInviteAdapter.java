package com.topia.chat.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.topia.chat.Activities.ImageViewerActivity;
import com.topia.chat.Activities.ServerSettingActivity;
import com.topia.chat.Models.ModelServerList;
import com.topia.chat.Models.ServerRequestModel;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerInviteAdapter extends RecyclerView.Adapter<ServerInviteAdapter.MyViewHolder> {
    Context context;
    ArrayList<ModelServerList> mlist;
    DatabaseReference DBref,SVref;
    SessionManager sessionManager;


    public ServerInviteAdapter(Context context, ArrayList<ModelServerList> mlist) {
        this.context = context;
        this.mlist = mlist;
        sessionManager = new SessionManager(context);
        DBref = FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        SVref = FirebaseDatabase.getInstance().getReference("servers");
    }

    @NonNull
    @Override
    public ServerInviteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_sv_invite,parent,false);

        return new ServerInviteAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerInviteAdapter.MyViewHolder holder, int position) {
        ModelServerList m = mlist.get(position);

        if(!m.getServerImage().equals("No Image")){
            Glide.with(context).load(m.getServerImage()).fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().into(holder.serverDP);
        }

        holder.serverDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!m.getServerImage().equals("No Image")){
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra("imageUrl",m.getServerImage());

                    context.startActivity(intent);

                }else{
                    Toast.makeText(context, "Server has no profile image", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.serverName.setText(m.getServerName());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SVref.child(m.getServerUid()).child("Members").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)).setValue(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));

                HashMap<String,String> mserver = new HashMap<>();
                mserver.put("serverID",m.getServerUid());
                mserver.put("status","member");
                DBref.child("servers").child(m.getServerUid()).setValue(mserver).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        DBref.child("server_invites").child(m.getServerUid()).setValue(null);
                        mlist.remove(position);
                        notifyDataSetChanged();

                    }
                });

            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBref.child("server_invites").child(m.getServerUid()).setValue(null);
                mlist.remove(position);
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView serverDP;
        TextView serverName;
        Button accept,delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serverDP = itemView.findViewById(R.id.serverDP);
            serverName = itemView.findViewById(R.id.Server_Name);
            accept = itemView.findViewById(R.id.accept_invite);
            delete = itemView.findViewById(R.id.delete_invite);


        }
    }

}
