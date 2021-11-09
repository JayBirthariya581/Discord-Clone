package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.topia.chat.Adapters.MessagesAdapter;
import com.topia.chat.Models.ChannelMessage;
import com.topia.chat.Models.Message;
import com.topia.chat.Models.ModelChannelMessage;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.databinding.ActivityChatBinding;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    String channelID,serverID,serverOwner,channelName,channelMemberCount;
    SessionManager sessionManager;
    ImageView back,attachment,addFriends;
    RecyclerView recyclerView;
    MaterialCardView sendButton;
    EditText messageBox;
    Toolbar toolbar;
    ActivityChatBinding binding;
    ArrayList<String> members;

    MessagesAdapter adapter;
    ArrayList<ModelChannelMessage> messages;
    TextView name;



    FirebaseStorage storage;
    DatabaseReference chatRef;
    ProgressDialog dialog;
    String senderUid,senderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_chat);


        getWindow().setStatusBarColor(ContextCompat.getColor(ChatActivity.this,R.color.black));
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        sessionManager = new SessionManager(ChatActivity.this);
        toolbar = findViewById(R.id.toolbar_c);


        setSupportActionBar(toolbar);

        //Receive Channel details
        channelID = getIntent().getStringExtra("channelID");
        serverID = getIntent().getStringExtra("serverID");
        channelName = getIntent().getStringExtra("channelName");
        channelMemberCount = getIntent().getStringExtra("channelMemberCount");
        serverOwner = getIntent().getStringExtra("serverOwner");
        senderName = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_USERNAME);
        senderUid = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);
        members = new ArrayList<>();


        //HOOKS
        name=findViewById(R.id.name_c);
        back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_channelChats);
        sendButton = findViewById(R.id.btn_send);
        messageBox = findViewById(R.id.messageBox);
        attachment = findViewById(R.id.attachment);
        addFriends = findViewById(R.id.add_friend_c);


        //Refrences
        chatRef = FirebaseDatabase.getInstance().getReference("servers").child(serverID).child("channels")
                .child(channelID)
                .child("chats");



        //Variables

        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);
        messages = new ArrayList<>();

        adapter = new MessagesAdapter(this, messages, chatRef);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.setAdapter(adapter);












        //Initailize Activity
            name.setText(channelName);

            //Get All Messages
            chatRef.child("messages").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messages.clear();
                    for(DataSnapshot snapshot1 : snapshot.getChildren()) {

                            ModelChannelMessage message = snapshot1.getValue(ModelChannelMessage.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);


                    }

                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });







        //Back Button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //Button to add friends to channel
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent =  new Intent(ChatActivity.this, ServerInviteFriendsActivity.class);

             intent.putExtra("serverID",serverID);
             intent.putExtra("channelID",channelID);
             startActivity(intent);
            }
        });




        //Add photos button
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });

        //Send Message Button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recyclerView.

                String messageTxt = messageBox.getText().toString();

                Date date = new Date();
                ChannelMessage message = new ChannelMessage(messageTxt, senderUid, date.getTime(),senderName);
                messageBox.setText("");

                String randomKey = chatRef.child("messages").push().getKey();

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                chatRef.updateChildren(lastMsgObj);




                chatRef.child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        recyclerView.scrollToPosition(messages.size() - 1);
                        sendNotification(channelName,message.getMessage(),"Admins");
                        sendNotification(channelName,message.getMessage(),"Members");
                    }
                });3

            }
        });




        final Handler handler = new Handler();

        //Enter Message box
        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                recyclerView.scrollToPosition(messages.size() - 1);
               // FirebaseDatabase.getInstance().getReference().child("presence").child(senderUid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);
            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    FirebaseDatabase.getInstance().getReference().child("presence").child(senderUid).setValue("Online");

                }
            };
        });




    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 25) {


            if(data != null) {
                if(data.getData() != null) {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();


                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    //here you can choose quality factor in third parameter(ex. i choosen 25)
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] fileInBytes = baos.toByteArray();




                    reference.putBytes(fileInBytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();

                                        //String messageTxt = messageBox.getText().toString();

                                        Date date = new Date();
                                        ChannelMessage message = new ChannelMessage("photo", senderUid, date.getTime(),senderName);

                                        message.setImageUrl(filePath);
                                        messageBox.setText("");

                                        String randomKey = chatRef.child("messages").push().getKey();

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());


                                        chatRef.updateChildren(lastMsgObj);

                                       chatRef.child("messages").child(randomKey)
                                                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   recyclerView.scrollToPosition(messages.size() - 1);
                                                   sendNotification(message.getSenderName(),"sent a photo","Admins");
                                                   sendNotification(message.getSenderName(),"sent a photo","Members");
                                               }
                                           }
                                       });

                                        //Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }



    void sendNotification(String N,String message,String cat){
        members.clear();
        FirebaseDatabase.getInstance().getReference("servers").child(serverID).child(cat).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot mem : snapshot.getChildren()){

                       members.add(mem.getValue(String.class));



                    }


                    for(int i=0;i<members.size();i++){

                        FirebaseDatabase.getInstance().getReference("users")
                                .child(members.get(i)).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot tok) {

                                if(!tok.getValue(String.class).equals(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_TOKEN)) && !tok.getValue(String.class).equals("-")){
                                    try {
                                        RequestQueue queue = Volley.newRequestQueue(ChatActivity.this);

                                        String url = "https://fcm.googleapis.com/fcm/send";

                                        JSONObject data = new JSONObject();

                                        data.put("title", N);
                                        data.put("body", message);

                                        JSONObject notificationData = new JSONObject();
                                        notificationData.put("notification",data);



                                        notificationData.put("to",tok.getValue(String.class));

                                        JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                // Toast.makeText(DirectChatActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }){

                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                HashMap<String,String> map = new HashMap<>();
                                                String key = "key=AAAAji6EONU:APA91bH9Y7H7sVtBc_gLti-WuYwFh1JxbT_bd9j2IKF2RvFi-WPevQZDSb-nI-uqauFJBzFeh5d3Q5uqV_zdzVt8HUTf4Ycb68ESKFJUPKS-id3ebc01sS9pPOvaTRPN3MKFVarosOIu";
                                                map.put("Authorization",key);
                                                map.put("Content-Type","application/json");

                                                return map;
                                            }
                                        };

                                        queue.add(request);

                                    }catch (Exception ex){

                                    }
                                }




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }





                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       if(getIntent().getStringExtra("adminStatus").equals("true")){
           getMenuInflater().inflate(R.menu.chat_menu, menu);
           return true;
       }else{
           return false;
       }



    }





    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearChat:

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setMessage(" Clear Chat ?");
                builder.setTitle("uTopia");
                builder.setIcon(R.drawable.main11);
                builder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        chatRef.child("messages").setValue(null);

                    }
                });
                builder.setCancelable(true);
                builder.show();
                item.setChecked(true);





                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }





}