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
import com.topia.chat.Adapters.DirectChatAdapter;
import com.topia.chat.Models.Message;
import com.topia.chat.Models.ModelChannelMessage;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.databinding.ActivityChatBinding;
import com.topia.chat.databinding.ActivityDirectChatBinding;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DirectChatActivity extends AppCompatActivity {

    SessionManager sessionManager;
    ImageView back;
    RecyclerView recyclerView;
    MaterialCardView sendButton;
    EditText messageBox;
    Toolbar toolbar;
    ActivityDirectChatBinding binding;
    ImageView attachment;

    DirectChatAdapter adapter;
    ArrayList<ModelChannelMessage> messages;
    TextView name;



    FirebaseStorage storage;
    DatabaseReference chatRef;
    ProgressDialog dialog;
    String senderUid,senderName;

    String friendName,friendUid,chatRoom;
    String token;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectChatBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_direct_chat);
        toolbar =(Toolbar) findViewById(R.id.toolbar_dc);
        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(ContextCompat.getColor(DirectChatActivity.this,R.color.black));
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);





        //HOOKS
        name=findViewById(R.id.name_dc);
        back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_directChats);
        sendButton = findViewById(R.id.btn_send);
        messageBox = findViewById(R.id.messageBox);
        attachment = findViewById(R.id.attachment);



        sessionManager = new SessionManager(DirectChatActivity.this);

        friendName = getIntent().getStringExtra("fnm");
        token = getIntent().getStringExtra("token");
        friendUid = getIntent().getStringExtra("fuid");
        senderName = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_USERNAME);
        senderUid = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);






        chatRoom = getIntent().getStringExtra("chatRoom");
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        messages = new ArrayList<>();




        name.setText(friendName);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        /*database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if(!status.isEmpty()) {
                        if(status.equals("Offline")) {
                            binding.status.setVisibility(View.GONE);
                        } else {
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


        chatRef = FirebaseDatabase.getInstance().getReference("directChats").child(chatRoom);
        adapter = new DirectChatAdapter(this, messages, chatRef);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.setAdapter(adapter);



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



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recyclerView.

                String messageTxt = messageBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime(),senderName);
                messageBox.setText("");

                String randomKey = chatRef.child("messages").push().getKey();

                HashMap<String, Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg", message.getMessage());
                lastMsgObj.put("lastMsgTime", date.getTime());

                chatRef.updateChildren(lastMsgObj);




                chatRef.child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendNotification(senderName,message.getMessage(),token);
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                });

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



        final Handler handler = new Handler();

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


        //getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    void sendNotification(String Name,String message,String token){

        try {
            RequestQueue queue = Volley.newRequestQueue(DirectChatActivity.this);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();

            data.put("title", senderName);
            data.put("body", message);

            JSONObject notificationData = new JSONObject();
            notificationData.put("notification",data);
            notificationData.put("to",token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   // Toast.makeText(DirectChatActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DirectChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

                                        String messageTxt = messageBox.getText().toString();

                                        Date date = new Date();
                                        Message message = new Message(messageTxt, senderUid, date.getTime(),senderName);
                                        message.setMessage("photo");
                                        message.setImageUrl(filePath);
                                        messageBox.setText("");

                                        String randomKey = chatRef.child("messages").push().getKey();

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());


                                        chatRef.updateChildren(lastMsgObj);

                                        chatRef.child("messages").child(randomKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sendNotification(senderName,"Sent a photo",token);
                                                recyclerView.scrollToPosition(messages.size() - 1);
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

    /*@Override
    protected void onResume() {
        super.onResume();
        String currentId = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);
        FirebaseDatabase.getInstance().getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID);
        FirebaseDatabase.getInstance().getReference().child("presence").child(currentId).setValue("Offline");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }





    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearChat:

                    AlertDialog.Builder builder = new AlertDialog.Builder(DirectChatActivity.this);
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