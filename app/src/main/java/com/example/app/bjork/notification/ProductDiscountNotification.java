package com.example.app.bjork.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.activity.ProductDetailActivity;
import com.example.app.bjork.model.Product;

import java.util.concurrent.ExecutionException;

public class ProductDiscountNotification {

    public static String DISCOUNT_NOTIFICATION_CHANNEL = "product_discount_notifications";

    private Context context;
    private Product product;

    public ProductDiscountNotification(Context context, Product product) {
        this.context = context;
        this.product = product;
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.discount_notification_channel_name);
            String description = context.getString(R.string.discount_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(DISCOUNT_NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void createNotification(){
        int notificationId = (int)System.currentTimeMillis()/1000;
        PendingIntent pendingIntent = createPendingIntent(product);
        String content = context.getString(R.string.discount_notification_text) + " " + product.getDiscountedPrice() + ",- Kč (-" + product.getDiscountPercentage()+"%)";
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DISCOUNT_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(product.getName())
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(getAddToCartAction(product))
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        loadNotificationImage(product, builder);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

    private PendingIntent createPendingIntent(Product product) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra("product", product);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void loadNotificationImage(Product product, NotificationCompat.Builder builder){
        try {
            Bitmap bitmap = Glide.with(context)
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

    public NotificationCompat.Action getAddToCartAction(Product product){
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra("addToCart", true);
        intent.putExtra("product", product);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action(R.drawable.ic_shopping_cart, context.getString(R.string.add_to_cart), pendingIntent);
    }

}
