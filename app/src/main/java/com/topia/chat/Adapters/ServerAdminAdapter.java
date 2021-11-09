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
import com.topia.chat.databinding.ServerAdminDialogBinding;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServerAdminAdapter extends RecyclerView.Adapter<ServerAdminAdapter.MyViewHolder> {
    Context context;
    ArrayList<UserTest> admin_list;
    String serverID;
    DatabaseReference SVref,DBref;

    public ServerAdminAdapter(Context context, ArrayList<UserTest> admin_list) {
        this.context = context;
        this.admin_list = admin_list;

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
        View v = LayoutInflater.from(context).inflate(R.layout.card_server_admin,parent,false);
        return new ServerAdminAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            UserTest a = admin_list.get(position);


        if(!a.getProfileImage().equals("No Image")){
            Glide.with(context).load(a.getProfileImage()).fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().into(holder.profileImage);
        }

        holder.adminName.setText(a.getUserName());


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View view_ = LayoutInflater.from(context).inflate(R.layout.server_admin_dialog, null);
                ServerAdminDialogBinding b = ServerAdminDialogBinding.bind(view_);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Admins")
                        .setView(b.getRoot())
                        .create();

                dialog.show();

               b.dismissAdmin.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       SVref.child(serverID).child("Members").child(a.getUid()).setValue(a.getUid());
                       SVref.child(serverID).child("Admins").child(a.getUid()).setValue(null);
                       DBref.child(a.getUid()).child("servers").child(serverID).child("status").setValue("member").addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               admin_list.remove(position);
                               notifyDataSetChanged();
                               dialog.dismiss();

                           }
                       });
                   }
               });

               b.RemoveAdmin.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       SVref.child(serverID).child("Members").child(a.getUid()).setValue(null);
                       SVref.child(serverID).child("Admins").child(a.getUid()).setValue(null);
                       DBref.child(a.getUid()).child("servers").child(serverID).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               admin_list.remove(position);
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
        return admin_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView adminName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.admin_profile);
            adminName = itemView.findViewById(R.id.admin_name);

        }
    }
}
