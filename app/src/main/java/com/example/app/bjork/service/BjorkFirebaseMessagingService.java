package com.example.app.bjork.service;

import com.example.app.bjork.api.BjorkAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class BjorkFirebaseMessagingService extends FirebaseMessagingService {

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
        System.out.println("message received: " + remoteMessage.getData());
    }
}
