package com.example.app.bjork.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;

import com.example.app.bjork.R;

import java.io.File;

public class DownloadImageNotification {

    public static String PROGRESS_NOTIFICATION_CHANNEL = "progress_notification_channel";
    private int id;
    private Bitmap bitmap;
    private Context context;
    private NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;

    public DownloadImageNotification(Context context, Bitmap bitmap) {
        this.id = (int)System.currentTimeMillis()/1000;
        this.bitmap = bitmap;
        this.context = context;
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.progress_notification_channel_name);
            String description = context.getString(R.string.progress_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(PROGRESS_NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void createNotification(){
        builder = new NotificationCompat.Builder(context, PROGRESS_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(context.getString(R.string.image_downloading))
                .setProgress(0, 0, true)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }

    public void downloadFailed(String message){
        builder.setProgress(0,0, false);
        builder.setContentText(message);
        notificationManager.notify(id, builder.build());
    }

    public void downloadCompleted(File file){
        builder.setProgress(0,0,false);
        builder.setContentText(context.getString(R.string.image_download_completed));
        builder.setLargeIcon(bitmap);
        builder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(bitmap)
                .bigLargeIcon(null))
                .setContentIntent(openImageIntent(file));
        notificationManager.notify(id, builder.build());
    }

    private PendingIntent openImageIntent(File file){
        Uri uri = FileProvider.getUriForFile(context, "com.example.app.bjork", file);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
