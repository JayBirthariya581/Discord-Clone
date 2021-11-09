package com.topia.chat.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.topia.chat.Activities.ImageViewerActivity;
import com.topia.chat.Models.ModelChannelMessage;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.databinding.DeleteDialogBinding;
import com.topia.chat.databinding.ItemReceiveBinding;
import com.topia.chat.databinding.ItemSentBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DirectChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ModelChannelMessage> messages;
    DatabaseReference chatref;
    SessionManager sessionManager;
    String userName;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;


    public DirectChatAdapter(Context context, ArrayList<ModelChannelMessage> messages, DatabaseReference chatref) {
        this.context = context;
        this.messages = messages;
        this.chatref=chatref;
        sessionManager = new SessionManager(context);
        userName = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_USERNAME);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        ModelChannelMessage message = messages.get(position);
        if(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID).equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelChannelMessage message = messages.get(position);








        if(holder.getClass() == SentViewHolder.class) {



            SentViewHolder viewHolder = (SentViewHolder) holder;

            if(message.getMessage().equals("photo")) {

                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.senderName.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);

                viewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ImageViewerActivity.class);
                        intent.putExtra("imageUrl",message.getImageUrl());

                        context.startActivity(intent);
                    }
                });


            }else{
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.senderName.setVisibility(View.GONE);
                viewHolder.binding.message.setVisibility(View.VISIBLE);

                viewHolder.binding.message.setText(message.getMessage());




            }


            long t = message.getTimestamp();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

            viewHolder.binding.messageTime.setText(simpleDateFormat.format(t));









            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                    DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(binding.getRoot())
                            .create();

                    binding.everyone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            message.setMessage("This message is removed.");

                            chatref.child("messages")
                                    .child(message.getMessageId()).setValue(message);

                            dialog.dismiss();
                        }
                    });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chatref.child("messages")
                                    .child(message.getMessageId()).setValue(null);
                            dialog.dismiss();
                        }
                    });

                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });
        }
        else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder)holder;
            viewHolder.binding.senderName.setVisibility(View.GONE);
            if(message.getMessage().equals("photo")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);

                viewHolder.binding.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ImageViewerActivity.class);
                        intent.putExtra("imageUrl",message.getImageUrl());

                        context.startActivity(intent);
                    }
                });





            }else{
                viewHolder.binding.image.setVisibility(View.GONE);


                viewHolder.binding.message.setText(message.getMessage());




            }
            long t = message.getTimestamp();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

            viewHolder.binding.messageTime.setText(simpleDateFormat.format(t));



            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_dialog, null);
                    DeleteDialogBinding binding = DeleteDialogBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(binding.getRoot())
                            .create();

                    binding.everyone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            message.setMessage("This message is removed.");

                            chatref.child("messages")
                                    .child(message.getMessageId()).setValue(message);

                            dialog.dismiss();
                        }
                    });

                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chatref.child("messages")
                                    .child(message.getMessageId()).setValue(null);
                            dialog.dismiss();
                        }
                    });

                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    return false;
                }
            });






        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }

}
