package com.topia.chat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Activities.MainContainerActivity;
import com.topia.chat.Helper.ServerInfoDisplay;
import com.topia.chat.Models.ModelServerList;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.ViewModels.PresenceViewModel;
import com.topia.chat.databinding.ServerCardDialogBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.MyViewHolder> {
    ArrayList<ModelServerList> mlist;

    Context context;
    ServerInfoDisplay serverInfoDisplay;
    SessionManager sessionManager;
    DatabaseReference SVref,DBref;
    PresenceViewModel presenceViewModel;






    public ServerListAdapter(ServerInfoDisplay serverInfoDisplay,ArrayList<ModelServerList> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;

        this.serverInfoDisplay = serverInfoDisplay;
        sessionManager = new SessionManager(context);
        DBref = FirebaseDatabase.getInstance().getReference("users");
        SVref = FirebaseDatabase.getInstance().getReference("servers");
    }


    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_server_list,parent,false);

        return new ServerListAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        ModelServerList m = mlist.get(position);
        //holder.serverName.setText(m.getServerName());*/

        if(!m.getServerImage().equals("No Image")) {
            Glide.with(context).load(m.getServerImage()).into(holder.circleImageView);

        }






        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                serverInfoDisplay.setMlistC(m.getServerUid());
                serverInfoDisplay.setServerName(m.getServerName());
                serverInfoDisplay.setCreate_channel(m.getServerUid());
                serverInfoDisplay.setInviteMembers(m.getServerUid());
                serverInfoDisplay.setServer_settings(m.getServerUid(),m.getServerName(),m.getServerImage());
                serverInfoDisplay.setServerDp(m.getServerImage(),context);


                Query check = SVref.child(m.getServerUid()).child("Admins").orderByValue().equalTo((sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID)));

                check.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            serverInfoDisplay.setAdminStatus("true");
                            serverInfoDisplay.getServer_settings().setVisibility(View.VISIBLE);
                        }else{
                            serverInfoDisplay.setAdminStatus("false");
                            serverInfoDisplay.getServer_settings().setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });












            }

        });






        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View view_ = LayoutInflater.from(context).inflate(R.layout.server_card_dialog, null);
                ServerCardDialogBinding b = ServerCardDialogBinding.bind(view_);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("uTopia")
                        .setView(b.getRoot())
                        .create();

                dialog.show();

                b.exitServer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Query check =  SVref.child(m.getServerUid()).child("Admins").orderByValue().equalTo(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));


                        check.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.exists()){

                                    SVref.child(m.getServerUid()).child("Admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot admins) {


                                            SVref.child(m.getServerUid()).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot members) {






                                                    if(admins.getChildrenCount()==1  ){

                                                        if(members.getChildrenCount()==0){
                                                            SVref.child(m.getServerUid()).child("Admins").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                                                                    .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    DBref.child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                                                                            .child("servers").child(m.getServerUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            SVref.child(m.getServerUid()).setValue(null);
                                                                            mlist.remove(position);
                                                                            notifyDataSetChanged();
                                                                            dialog.dismiss();
                                                                            Intent intent = new Intent(context, MainContainerActivity.class);
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            context.startActivity(intent);


                                                                        }
                                                                    });
                                                                }
                                                            });

                                                        }else{

                                                            AlertDialog.Builder builder =  new AlertDialog.Builder(context);
                                                            builder.setTitle("Cannot exit");
                                                            builder.setMessage("As you are the only admin\nMake someone else admin ");
                                                            dialog.dismiss();
                                                            builder.show();


                                                        }




                                                    }


                                                    else{


                                                        SVref.child(m.getServerUid()).child("Admins").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                                                                .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                DBref.child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                                                                        .child("servers").child(m.getServerUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        mlist.remove(position);
                                                                        notifyDataSetChanged();
                                                                        dialog.dismiss();
                                                                        Intent intent = new Intent(context, MainContainerActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        context.startActivity(intent);
                                                                    }
                                                                });
                                                            }
                                                        });


                                                    }







                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });



                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });




                                }else{


                                    SVref.child(m.getServerUid()).child("Members").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                                            .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            DBref.child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                                                    .child("servers").child(m.getServerUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mlist.remove(position);
                                                    notifyDataSetChanged();
                                                    dialog.dismiss();
                                                    Intent intent = new Intent(context, MainContainerActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    context.startActivity(intent);
                                                }
                                            });
                                        }
                                    });


                                }



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });











                    }
                });





                return true;
            }
        });





        if(position==0){
            serverInfoDisplay.getMlistC().clear();
            holder.itemView.performClick();
        }

    }



    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHolder extends  RecyclerView.ViewHolder{
        TextView serverName;
        MaterialCardView materialCardView;
        ImageView circleImageView;


        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            //serverName = itemView.findViewById(R.id.server_name);
            circleImageView = itemView.findViewById(R.id.imageViewServer);
            materialCardView = itemView.findViewById(R.id.server_list_card_main);

        }
    }
}
