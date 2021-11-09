package com.topia.chat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.topia.chat.Models.UserTest;
import com.topia.chat.R;
import com.topia.chat.RenameChannelActivity;
import com.topia.chat.databinding.DeleteChannelBinding;
import com.topia.chat.databinding.ServerMemberDialogBinding;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServerMemberAdapter extends  RecyclerView.Adapter<ServerMemberAdapter.MyViewHolder> {
Context context;
ArrayList<UserTest> member_list;
String serverID;
DatabaseReference SVref,DBref;


    public ServerMemberAdapter(Context context, ArrayList<UserTest> member_list) {
        this.context = context;
        this.member_list = member_list;

        DBref = FirebaseDatabase.getInstance().getReference("users");
        SVref = FirebaseDatabase.getInstance().getReference("servers");

    }



    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_server_member,parent,false);
        return new ServerMemberAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        UserTest u = member_list.get(position);


        if(!u.getProfileImage().equals("No Image")){
            Glide.with(context).load(u.getProfileImage()).fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().into(holder.profileImage);
        }

        holder.memberName.setText(u.getUserName());


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View view_ = LayoutInflater.from(context).inflate(R.layout.server_member_dialog, null);
                ServerMemberDialogBinding b = ServerMemberDialogBinding.bind(view_);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Members")
                        .setView(b.getRoot())
                        .create();

                dialog.show();

                b.MakeAdmin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SVref.child(serverID).child("Members").child(u.getUid()).setValue(null);
                        SVref.child(serverID).child("Admins").child(u.getUid()).setValue(u.getUid());
                        DBref.child(u.getUid()).child("servers").child(serverID).child("status").setValue("admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                member_list.remove(position);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                });


                b.RemoveMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SVref.child(serverID).child("Members").child(u.getUid()).setValue(null);
                        DBref.child(u.getUid()).child("servers").child(serverID).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                member_list.remove(position);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });



                    }
                });


                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return member_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView memberName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.member_profile);
            memberName = itemView.findViewById(R.id.member_name);

        }
    }
}
