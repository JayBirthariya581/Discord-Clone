package com.topia.chat.Adapters;

import android.annotation.SuppressLint;
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
import com.topia.chat.Models.ModelChannelList;
import com.topia.chat.Models.RequestSenderModel;
import com.topia.chat.Models.UserTest;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder>{
    ArrayList<RequestSenderModel> mlist;
    Context context;

    DatabaseReference DBref;
    SessionManager sessionManager;
    HashMap<String,String> UserDetails;

    public FriendRequestAdapter(ArrayList<RequestSenderModel> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;


        sessionManager = new SessionManager(context);
        UserDetails = sessionManager.getUsersDetailsFromSessions();
        DBref = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_friend_request,parent,false);

        return new FriendRequestAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RequestSenderModel sender = mlist.get(position);

        if(!sender.getProfileImage().equals("No Image")){
            Glide.with(context).load(sender.getProfileImage()).fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate().into(holder.imageURL);
        }


        holder.userName.setText(sender.getUserName());

        holder.imageURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sender.getProfileImage().equals("No Image")){
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra("imageUrl",sender.getProfileImage());

                    context.startActivity(intent);

                }else{
                    Toast.makeText(context, "User has no profile image", Toast.LENGTH_SHORT).show();
                }




            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendList")
                        .child(sender.getUid()).setValue(sender.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendRequests").child(sender.getUid())
                                    .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    DBref.child(sender.getUid()).child("Friends").child("FriendList")
                                            .child(UserDetails.get(SessionManager.KEY_UID)).setValue(UserDetails.get(SessionManager.KEY_UID))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        mlist.remove(position);
                                                        notifyDataSetChanged();

                                                        String dcKey = DBref.push().getKey();
                                                        DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("DirectChats").child(sender.getUid())
                                                                .setValue(dcKey);

                                                        DBref.child(sender.getUid()).child("DirectChats").child(UserDetails.get(SessionManager.KEY_UID))
                                                                .setValue(dcKey);








                                                        Toast.makeText(context, "Added to friend list", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            });




                        }
                    }
                });





            }
        });


        holder.ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendRequests").child(sender.getUid())
                        .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mlist.remove(position);
                        notifyDataSetChanged();

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
        Button accept,ignore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageURL = itemView.findViewById(R.id.imageViewSender);
            userName = itemView.findViewById(R.id.sender_name);
            accept = itemView.findViewById(R.id.accept_request);
            ignore = itemView.findViewById(R.id.ignore_request);

        }
    }
}