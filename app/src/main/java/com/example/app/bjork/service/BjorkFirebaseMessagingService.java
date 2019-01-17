package com.example.app.bjork.service;

import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Product;
import com.example.app.bjork.notification.ProductDiscountNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Map;

public class BjorkFirebaseMessagingService extends FirebaseMessagingService {

    private static String NOTIFICATION_PAYLOAD_DATA = "product";

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
        ProductDiscountNotification notification = new ProductDiscountNotification(this, product);
        notification.createNotificationChannel();
        notification.createNotification();
    }

    public Product parseProductFromPayload(Map<String, String> payload){
        Gson gson = new Gson();
        String stringifyProduct = payload.get(NOTIFICATION_PAYLOAD_DATA);
        JsonElement jsonElement = new JsonParser().parse(stringifyProduct);
        return gson.fromJson(jsonElement, Product.class);
    }

}
