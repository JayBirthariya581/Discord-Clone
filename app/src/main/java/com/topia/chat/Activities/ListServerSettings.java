package com.topia.chat.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.topia.chat.R;

public class ListServerSettings extends AppCompatActivity {
    String heading,list_ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_server_settings);

        heading = getIntent().getStringExtra("heading");
        list_ref = getIntent().getStringExtra("list_ref");
    }
}