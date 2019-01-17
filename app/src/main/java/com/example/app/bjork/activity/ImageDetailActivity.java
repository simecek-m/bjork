package com.example.app.bjork.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.app.bjork.R;
import com.example.app.bjork.constant.Constant;
import com.example.app.bjork.model.Product;
import com.example.app.bjork.viewmodel.ImageDetailViewModel;
import com.example.app.bjork.notification.DownloadImageNotification;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class ImageDetailActivity extends AppCompatActivity {

    public static int EXTERNAL_STORAGE_REQUEST = 1;
    private static final String TAG = "ImageDetailActivity";

    private ImageDetailViewModel imageDetailViewModel;
    private Product product;
    private Bitmap bitmap;

    private ImageView likeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageDetailViewModel = ViewModelProviders.of(this).get(ImageDetailViewModel.class);

        product = (Product)getIntent().getSerializableExtra("product");

        PhotoView photoView = findViewById(R.id.photoView);
        ImageView shareView = findViewById(R.id.share);
        ImageView downloadView = findViewById(R.id.download);
        likeView = findViewById(R.id.like);

        initialLikeButton();

        Glide.with(this)
                .load(product.getImageUrl())
                .into(photoView);

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(view)
                        .asFile()
                        .load(product.getImageUrl())
                        .into(new SimpleTarget<File>() {
                            @Override
                            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                shareImage(resource);
                            }
                        });
            }
        });

        downloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(ImageDetailActivity.this)
                        .asBitmap()
                        .load(product.getImageUrl())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bitmap = resource;
                                checkPermissionAndDownload(bitmap);
                            }
                        });
            }
        });

        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageDetailViewModel.likeProduct(product);
            }
        });
    }

    private void initialLikeButton() {
        if(imageDetailViewModel.isLikedByUser(product)){
            likeView.setImageResource(R.drawable.ic_favorite_heart);
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageDetailViewModel.getImageDownloaded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean imageDownloaded) {
                if(imageDownloaded != null && imageDownloaded){
                    showToast(getString(R.string.image_downloaded));
                }
            }
        });

        imageDetailViewModel.getLikedByUser().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean likedByUser) {
                if(likedByUser == null){
                    showToast(getString(R.string.unlogged_user));
                }else if(likedByUser){
                    likeView.setImageResource(R.drawable.ic_favorite_heart);
                }else{
                    likeView.setImageResource(R.drawable.ic_heart);
                }
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ImageDetailActivity.this);
                Intent intent = new Intent(Constant.BROADCAST_LIKE_PRODUCT);
                intent.putExtra("product", product);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        imageDetailViewModel.getImageDownloaded().removeObservers(this);
        imageDetailViewModel.getLikedByUser().removeObservers(this);
    }

    public void checkPermissionAndDownload(Bitmap bitmap){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            imageDetailViewModel.downloadImage(bitmap);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == EXTERNAL_STORAGE_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                imageDetailViewModel.downloadImage(bitmap);
            }
        }
    }

    public void shareImage(File file){
        if(file.exists()){
            Uri uriImage = FileProvider.getUriForFile(this, "com.example.app.bjork", file);
            Intent shareIntent = new Intent();
            shareIntent.setType("image/jpeg");
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriImage);
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_with)));
        }else{
            Log.e(TAG, "shareImage: image not in cache");
        }
    }

    public void showCorrectLikeIcon(){
        if(product.likedByUser(currentUserId)){
            likeView.setImageResource(R.drawable.ic_favorite_heart);
        }else{
            likeView.setImageResource(R.drawable.ic_heart);
        }
    }
}