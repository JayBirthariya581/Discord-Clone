package com.topia.chat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.topia.chat.R;
import com.topia.chat.databinding.ActivityChatDemoBinding;

public class ChatActivityDemo extends AppCompatActivity {
    ActivityChatDemoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDemoBinding.inflate(getLayoutInflater());
        setSupportActionBar(binding.toolbar);
        getWindow().setStatusBarColor(ContextCompat.getColor(ChatActivityDemo.this, R.color.status2));
        setContentView(binding.getRoot());
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_demo,menu);
        return true;
    }





}