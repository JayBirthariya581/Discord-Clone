package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.databinding.ActivityCreateAnnouncementBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class CreateAnnouncementActivity extends AppCompatActivity {
    ActivityCreateAnnouncementBinding binding;
    DatabaseReference DBref;
    SessionManager sessionManager;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateAnnouncementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(ContextCompat.getColor(CreateAnnouncementActivity.this, R.color.status2));

        DBref = FirebaseDatabase.getInstance().getReference("owners");
        sessionManager = new SessionManager(CreateAnnouncementActivity.this);
        progressDialog = new ProgressDialog(CreateAnnouncementActivity.this);
        progressDialog.setMessage("Please wait");
        binding.CreateAnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAnnouncement();
            }
        });

    }

    private void createAnnouncement() {
        progressDialog.show();
        if(binding.annBody.getEditText().getText().toString().isEmpty()){
            binding.annBody.setError("Field cannot be empty");
            progressDialog.dismiss();
            return;
        }

        Query checkOwner = DBref.child("ownerList").orderByValue().equalTo(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL));


        checkOwner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm");
                    String key = DBref.child("announcements").push().getKey();
                    HashMap<String,String> h=new HashMap<>();
                    h.put("body",binding.annBody.getEditText().getText().toString());
                    h.put("annID",key);
                    h.put("time",simpleDateFormat.format(date.getTime()));


                    DBref.child("announcements").child(key).setValue(h).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                binding.annBody.setError(null);
                                binding.annBody.getEditText().setText("");

                                Toast.makeText(CreateAnnouncementActivity.this, "Announcement Created", Toast.LENGTH_SHORT).show();


                            }else{
                                progressDialog.dismiss();
                            }


                        }
                    });








                }else{
                    progressDialog.dismiss();
                    Toast.makeText(CreateAnnouncementActivity.this, sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL), Toast.LENGTH_SHORT).show();

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}