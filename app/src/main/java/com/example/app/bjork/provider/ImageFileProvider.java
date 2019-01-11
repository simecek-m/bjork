package com.example.app.bjork.provider;

import android.net.Uri;
import android.support.annotation.NonNull;

public class ImageFileProvider extends android.support.v4.content.FileProvider {
    @Override
    public String getType(@NonNull Uri uri) {
        return "image/jpeg";
    }
}