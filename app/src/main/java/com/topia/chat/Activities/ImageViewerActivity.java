package com.topia.chat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.topia.chat.R;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        getWindow().setStatusBarColor(ContextCompat.getColor(ImageViewerActivity.this,R.color.status2));

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        //photoView.setImageResource(R.drawable.ic_main2);
        Glide.with(ImageViewerActivity.this)
                .load(getIntent().getStringExtra("imageUrl"))
                .placeholder(R.drawable.placeholder)
                .into(photoView);

    }
}