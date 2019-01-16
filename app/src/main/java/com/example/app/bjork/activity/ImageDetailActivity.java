package com.example.app.bjork.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.app.bjork.R;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.constant.Constant;
import com.example.app.bjork.model.Product;
import com.example.app.bjork.notification.DownloadImageNotification;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;

public class ImageDetailActivity extends AppCompatActivity {

    private static final String TAG = "ImageDetailActivity";
    private static final String ALBUM_NAME = "Bjork";
    public static int EXTERNAL_STORAGE_REQUEST = 1;

    private Product product;

    private PhotoView photoView;
    private ImageView shareView;
    private ImageView downloadView;
    private ImageView likeView;
    private String currentUserId;

    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        product = (Product)getIntent().getSerializableExtra("product");
        photoView = findViewById(R.id.photoView);
        shareView = findViewById(R.id.share);
        downloadView = findViewById(R.id.download);
        likeView = findViewById(R.id.like);
        currentUserId = FirebaseAuth.getInstance().getUid();

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
                                checkPermissionAndDownload(resource);
                            }
                        });
            }
        });

        showCorrectLikeIcon();

        likeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.likeProduct(currentUserId);
                BjorkAPI.updateLikes(product);
                showCorrectLikeIcon();
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ImageDetailActivity.this);
                Intent intent = new Intent(Constant.BROADCAST_LIKE_PRODUCT);
                intent.putExtra("product", product);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    public void downloadImage(Bitmap bitmap){
        DownloadImageNotification downloadImageNotification = new DownloadImageNotification(this, bitmap);
        downloadImageNotification.createNotificationChannel();
        downloadImageNotification.createNotification();

        if(isExternalStorageWritable()){
            File album = getPublicAlbumStorageDir(ALBUM_NAME);
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(album, fileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                downloadImageNotification.downloadCompleted(file);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "downloadImage: ", e);
                downloadImageNotification.downloadFailed();
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        file.mkdirs();
        return file;
    }

    public void checkPermissionAndDownload(Bitmap bitmap){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            downloadImage(bitmap);
        }else{
            this.bitmap = bitmap;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == EXTERNAL_STORAGE_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                downloadImage(bitmap);
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