package com.example.app.bjork.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.app.bjork.R;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Product;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ImageDetailActivity extends AppCompatActivity {

    private static final String TAG = "ImageDetailActivity";
    private static final String ALBUM_NAME = "Bjork";
    public static int EXTERNAL_STORAGE_REQUEST = 1;

    private Product product;

    private PhotoView photoView;
    private ImageView shareView;
    private ImageView downloadView;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        product = (Product)getIntent().getSerializableExtra("product");
        photoView = findViewById(R.id.photoView);
        shareView = findViewById(R.id.share);
        downloadView = findViewById(R.id.download);

        Glide.with(this)
                .load(product.getImageUrl())
                .into(photoView);

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(), getString(R.string.feature_not_available), Toast.LENGTH_SHORT).show();
            }
        });

        downloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getBaseContext())
                        .asBitmap()
                        .load(product.getImageUrl())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bitmap = resource;
                                checkExternalStoragePermission();
                            }
                        });
            }
        });
    }

    public void downloadImage(Bitmap resource){
        if(isExternalStorageWritable()){
            File album = getPublicAlbumStorageDir(ALBUM_NAME);
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(album, fileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Toast toast = Toast.makeText(getBaseContext(), getString(R.string.image_downloaded), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CLIP_VERTICAL, 0, 180);
                toast.show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "downloadImage: ", e);
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

    public void checkExternalStoragePermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            downloadImage(bitmap);
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
                downloadImage(bitmap);
            }
        }
    }
}
