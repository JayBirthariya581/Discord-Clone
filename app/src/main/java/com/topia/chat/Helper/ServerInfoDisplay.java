package com.topia.chat.Helper;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Activities.CreateChannelActivity;
import com.topia.chat.Activities.ImageViewerActivity;
import com.topia.chat.Activities.ServerInviteFriendsActivity;
import com.topia.chat.Activities.ServerInviteMemberActivity;
import com.topia.chat.Activities.ServerSettingActivity;
import com.topia.chat.Adapters.ChannelListAdapter;
import com.topia.chat.Models.ModelChannelList;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServerInfoDisplay {

    String adminStatus;
    Context context;
    TextView serverName;
    ImageView server_settings;
    Button create_channel;
    Button InviteMembers;
    ArrayList<ModelChannelList> mlistC;
    ImageView serverDp;

    DatabaseReference serverRef;
    ChannelListAdapter adapterC;




    public ServerInfoDisplay(Context context, TextView serverName, Button create_channel, Button inviteMembers, ArrayList<ModelChannelList> mlistC, ChannelListAdapter adapterC,ImageView server_settings,ImageView serverDp) {
        this.context = context;
        this.serverName = serverName;
        this.create_channel = create_channel;
        this.InviteMembers = inviteMembers;
        this.mlistC = mlistC;
        this.adapterC = adapterC;
        this.serverDp= serverDp;
        this.server_settings = server_settings;
    }

    public ImageView getServerDp() {
        return serverDp;
    }

    public void setServerDp(String serverImage,Context context1) {
        if(!serverImage.equals("No Image")){
            Glide.with(context1).load(serverImage).placeholder(R.drawable.main12).into(serverDp);
            serverDp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra("imageUrl",serverImage);

                    context.startActivity(intent);
                }
            });

        }else{

            serverDp.setImageResource(R.drawable.main12);
            serverDp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Server has no profile image", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public String getAdminStatus() {

        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        adapterC.setAdminStatus(adminStatus);
        this.adminStatus = adminStatus;
    }

    public ImageView getServer_settings() {
        return server_settings;
    }

    public void setServer_settings(String serverID,String serverName,String serverimage) {

        server_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ServerSettingActivity.class);
                intent.putExtra("serverID",serverID);
                intent.putExtra("serverName",serverName);
                intent.putExtra("serverImage",serverimage);

                context.startActivity(intent);
            }
        });




    }

    public Button getInviteMembers() {
        return InviteMembers;
    }

    public void setInviteMembers(String serverID) {

        //Toast.makeText(context, serverID, Toast.LENGTH_SHORT).show();
        this.InviteMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ServerInviteFriendsActivity.class);
                intent.putExtra("serverID",serverID);
                //intent.putExtra("serverProfile",severProfile);

                context.startActivity(intent);
            }
        });
    }

    public TextView getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName.setText(serverName);
    }

    public Button getCreate_channel() {
        return create_channel;
    }

    public void setCreate_channel(String serverID) {
        this.create_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateChannelActivity.class);
                intent.putExtra("server_ID",serverID);
                context.startActivity(intent);
            }
        });
    }

    public ArrayList<ModelChannelList> getMlistC() {
        return mlistC;
    }

    public void setMlistC(String serverID) {
        adapterC.setServerID(serverID);
        mlistC.clear();

        serverRef = FirebaseDatabase.getInstance().getReference("servers").child(serverID).child("channels");

        serverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mlistC.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelChannelList m = dataSnapshot.getValue(ModelChannelList.class);
                    m.setChannelID(dataSnapshot.getKey());

                    mlistC.add(m);
                }
                adapterC.notifyDataSetChanged();


            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public ChannelListAdapter getAdapterC() {
        return adapterC;
    }

    public void setAdapterC(ChannelListAdapter adapterC) {
        this.adapterC = adapterC;
    }
}


