package com.example.app.bjork.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.activity.ProductDetailActivity;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BjorkFirebaseMessagingService extends FirebaseMessagingService {

    private static String NOTIFICATION_PAYLOAD_DATA = "product";
    private static String DISCOUNT_CHANNEL = "product";

    @Override
    public void onNewToken(String token) {
        updateMessagingToken(token);
    }

    public void updateMessagingToken(String token){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getUid();
        if(currentUserId != null){
            BjorkAPI.updateMessagingToken(currentUserId, token);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Product product = parseProductFromPayload(remoteMessage.getData());
        createNotificationChannel();
        createNotification(product);
    }

    public Product parseProductFromPayload(Map<String, String> payload){
        Gson gson = new Gson();
        String stringifyProduct = payload.get(NOTIFICATION_PAYLOAD_DATA);
        JsonElement jsonElement = new JsonParser().parse(stringifyProduct);
        return gson.fromJson(jsonElement, Product.class);
    }

    public void createNotification(Product product){
        int notificationId = (int)System.currentTimeMillis()/1000;
        PendingIntent pendingIntent = createPendingIntent(product);
        String content = getString(R.string.discount_notification_text) + " " + product.getDiscountedPrice() + ",- Kč (-" + product.getDiscountPercentage()+"%)";
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DISCOUNT_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(product.getName())
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(getAddToCartAction(product))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        loadNotificationImage(product, builder);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
    }

    private PendingIntent createPendingIntent(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public NotificationCompat.Action getAddToCartAction(Product product){
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("addToCart", true);
        intent.putExtra("product", product);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action(R.drawable.ic_shopping_cart, getString(R.string.add_to_cart), pendingIntent);
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.discount_notification_channel_name);
            String description = getString(R.string.discount_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(DISCOUNT_CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void loadNotificationImage(Product product, NotificationCompat.Builder builder){
        try {
            Bitmap bitmap = Glide.with(this)
                    .asBitmap()
                    .load(product.getImageUrl())
                    .submit()
                    .get();
            builder.setLargeIcon(bitmap);
            builder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
