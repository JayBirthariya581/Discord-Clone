package com.topia.chat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.topia.chat.Activities.ChatActivity;
import com.topia.chat.Activities.GroupChatActivity;
import com.topia.chat.Models.ModelChannelList;
import com.topia.chat.Models.ModelServerList;
import com.topia.chat.R;
import com.topia.chat.RenameChannelActivity;
import com.topia.chat.databinding.DeleteChannelBinding;
import com.topia.chat.databinding.DeleteDialogBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelListAdapter extends RecyclerView.Adapter<ChannelListAdapter.MyViewHolder> {
    ArrayList<ModelChannelList> mlist;
    Context context;
    String serverID,serverOwner;
    DatabaseReference serverRef;
    String adminStatus;

    public String getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        this.adminStatus = adminStatus;
    }

    public String getServerOwner() {
        return serverOwner;
    }

    public void setServerOwner(String serverOwner) {
        this.serverOwner = serverOwner;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public ChannelListAdapter(ArrayList<ModelChannelList> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;
        serverRef = FirebaseDatabase.getInstance().getReference("servers");
    }

    @NonNull
    @NotNull
    @Override
    public ChannelListAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_channel_list,parent,false);

        return new ChannelListAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChannelListAdapter.MyViewHolder holder, int position) {
        ModelChannelList m = mlist.get(position);
        holder.channelName.setText(m.getChannelName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);

                if(adminStatus.equals("true")){
                    intent.putExtra("adminStatus","true");
                }else{
                    intent.putExtra("adminStatus","false");
                }

                intent.putExtra("serverID",ChannelListAdapter.this.serverID);
                intent.putExtra("serverOwner",ChannelListAdapter.this.serverOwner);
                intent.putExtra("channelID",m.getChannelID());
                intent.putExtra("channelName",m.getChannelName());
                intent.putExtra("channelMemberCount",m.getMemberCount());
                context.startActivity(intent);

                //Toast.makeText(context, serverOwner, Toast.LENGTH_SHORT).show();
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View view_ = LayoutInflater.from(context).inflate(R.layout.delete_channel, null);
                DeleteChannelBinding b = DeleteChannelBinding.bind(view_);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Channel")
                        .setView(b.getRoot())
                        .create();

                dialog.show();

                b.deleteChannel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        serverRef.child(serverID).child("channels").child(m.getChannelID()).setValue(null);
                        dialog.dismiss();
                    }
                });

                b.renameChannel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RenameChannelActivity.class);
                        intent.putExtra("serverID",serverID);
                        intent.putExtra("channelID",m.getChannelID());

                        context.startActivity(intent);
                    }
                });


                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }




    class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView channelName;
        MaterialCardView materialCardView;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            channelName = itemView.findViewById(R.id.channelCard_name);

            materialCardView = itemView.findViewById(R.id.channel_list_card_main);

        }
    }
}
