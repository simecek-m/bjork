package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.Product;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDetailViewModel extends ViewModel {

    private static final String TAG = "ImageDetailViewModel";
    private static final String ALBUM_NAME = "Bjork";

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private MutableLiveData<Boolean> likedByUser = new MutableLiveData<>();

    public void likeProduct(Product product) {
        String currentUserId = auth.getUid();
        if(currentUserId != null){
            product.likeProduct(currentUserId);
            Database.updateLikes(product);
            likedByUser.setValue(product.likedByUser(currentUserId));
        }else{
            likedByUser.setValue(null);
        }
    }

    public File downloadImage(Bitmap bitmap) {
        if(isExternalStorageWritable()){
            File album = getPublicAlbumStorageDir(ALBUM_NAME);
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(album, fileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "downloadImage: ", e);
                return null;
            }
        }else{
            return null;
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File getPublicAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        file.mkdirs();
        return file;
    }

    public MutableLiveData<Boolean> getLikedByUser() {
        return likedByUser;
    }

    public boolean isLikedByUser(Product product){
        String currentUserId = auth.getUid();
        return product.likedByUser(currentUserId);
    }
}
