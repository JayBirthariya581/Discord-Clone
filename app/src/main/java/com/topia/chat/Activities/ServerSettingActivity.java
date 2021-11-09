package com.topia.chat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServerSettingActivity extends AppCompatActivity {
    Button admin,members,updateServer;
    TextView serverName;
    CircleImageView serverDp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);

        getWindow().setStatusBarColor(ContextCompat.getColor(ServerSettingActivity.this,R.color.status2));
        serverDp = findViewById(R.id.profileimage);
        admin = findViewById(R.id.admin_list);
        members = findViewById(R.id.member_list);
        serverName = findViewById(R.id.server_setting_head);
        updateServer = findViewById(R.id.update_server);

        serverName.setText(getIntent().getStringExtra("serverName"));
        Intent intent =  new Intent(ServerSettingActivity.this,ServerInfoListActivity.class);
        if(!getIntent().getStringExtra("serverImage").equals("No Image")){
            Glide.with(ServerSettingActivity.this).load(getIntent().getStringExtra("serverImage")).placeholder(R.drawable.ic_main1).into(serverDp);


        }

        serverDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(!getIntent().getStringExtra("serverImage").equals("No Image")){
                    Intent intent = new Intent(ServerSettingActivity.this, ImageViewerActivity.class);
                    intent.putExtra("imageUrl",getIntent().getStringExtra("serverImage"));

                    startActivity(intent);

                }else{
                    Toast.makeText(ServerSettingActivity.this, "Server has no profile image", Toast.LENGTH_SHORT).show();
                }


            }
        });



        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("type","admin");
                intent.putExtra("serverID",getIntent().getStringExtra("serverID"));
                startActivity(intent);
            }
        });


        updateServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentu = new Intent(ServerSettingActivity.this,UpdateServerActivity.class);
                intentu.putExtra("serverName",getIntent().getStringExtra("serverName"));
                intentu.putExtra("serverImage",getIntent().getStringExtra("serverImage"));
                intentu.putExtra("serverID",getIntent().getStringExtra("serverID"));
                startActivity(intentu);
            }
        });


        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("type","member");
                intent.putExtra("serverID",getIntent().getStringExtra("serverID"));
                startActivity(intent);
            }
        });

    }
}