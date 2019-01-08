package com.example.app.bjork.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Product;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ImageDetailActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Product product;
    private boolean startFromSearchActivity;
    private String currentUserId;

    private PhotoView photoView;
    private ImageView shareView;
    private ImageView likeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getUid();
        product = (Product)getIntent().getSerializableExtra("product");
        startFromSearchActivity = getIntent().getBooleanExtra("startFromSearchActivity", false);
        photoView = findViewById(R.id.photoView);
        shareView = findViewById(R.id.share);
        likeView = findViewById(R.id.like);

        Glide.with(this)
                .load(product.getImageUrl())
                .into(photoView);

        if(product.getLikes().contains(currentUserId)){
            likeView.setImageResource(R.drawable.ic_favorite_heart);
        }else{
            likeView.setImageResource(R.drawable.ic_heart);
        }

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), getString(R.string.feature_not_available), Toast.LENGTH_SHORT).show();
            }
        });

        if (auth.getUid() == null || startFromSearchActivity){
            likeView.setVisibility(View.GONE);
        }else{
            likeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    like();
                }
            });
        }
    }

    public void like(){
        List<String> likes = product.getLikes();
        if(likes.contains(auth.getUid())){
            likes.remove(currentUserId);
            likeView.setImageResource(R.drawable.ic_heart);

        }else{
            likes.add(currentUserId);
            likeView.setImageResource(R.drawable.ic_favorite_heart);
        }
        BjorkAPI.likeProduct(product);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("product", product);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
