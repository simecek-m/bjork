package com.example.app.bjork.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.model.Product;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        Product product = (Product)getIntent().getSerializableExtra("product");
        PhotoView photoView = findViewById(R.id.photoView);
        Glide.with(this)
                .load(product.getImageUrl())
                .into(photoView);
    }
}
