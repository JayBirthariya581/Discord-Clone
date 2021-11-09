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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Activities.DirectChatActivity;
import com.topia.chat.Activities.ImageViewerActivity;
import com.topia.chat.Models.RequestSenderModel;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder>{
    ArrayList<RequestSenderModel> mlist;
    Context context;

    DatabaseReference DBref;
    SessionManager sessionManager;
    HashMap<String,String> UserDetails;

    public FriendListAdapter(ArrayList<RequestSenderModel> mlist, Context context) {
        this.mlist = mlist;
        this.context = context;


        sessionManager = new SessionManager(context);
        UserDetails = sessionManager.getUsersDetailsFromSessions();
        DBref = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_friend_list,parent,false);

        return new FriendListAdapter.MyViewHolder(v);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                        .child("DirectChats").child(sender.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent = new Intent(context,DirectChatActivity.class);
                        intent.putExtra("fnm",sender.getUserName());
                        intent.putExtra("fuid",sender.getUid());
                        intent.putExtra("chatRoom",snapshot.getValue(String.class));

                        intent.putExtra("token",sender.getToken());



                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });







        FirebaseDatabase.getInstance().getReference("presence").child(sender.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.status.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageURL,directChat;
        TextView userName;
        TextView status;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageURL = itemView.findViewById(R.id.imageViewSender);
            userName = itemView.findViewById(R.id.sender_name);
            status = itemView.findViewById(R.id.presence);
            directChat = itemView.findViewById(R.id.directChat);


        }
    }
}