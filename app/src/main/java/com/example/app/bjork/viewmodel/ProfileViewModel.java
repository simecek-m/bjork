package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.UserInfo;
import com.example.app.bjork.wrapper.DataWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileViewModel extends ViewModel {

    public static final int UPDATE_MESSAGING_TOKEN_ERROR = 1;
    public static final int UPDATE_USER_INFO_ERROR = 2;

    public static final String UPDATED_USER_INFO_MESSAGE = "updated-user-info-message";

    private MutableLiveData<DataWrapper<Boolean>> loggedOut = new MutableLiveData<>();
    private MutableLiveData<DataWrapper<UserInfo>> currentUserInfo = new MutableLiveData<>();

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public FirebaseUser getCurrentUser(){
        return currentUser;
    }

    public void logout(){
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        Database.updateMessagingToken(auth.getUid(), null).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                auth.signOut();
                loggedOut.setValue(new DataWrapper<>(true));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loggedOut.setValue(new DataWrapper<Boolean>(UPDATE_MESSAGING_TOKEN_ERROR));
            }
        });
    }

    public MutableLiveData<DataWrapper<Boolean>> getLoggedOut(){
        return loggedOut;
    }

    public void updateUserInfo(String firstname, String lastname, String address, String gender){
        FirebaseUser currentUser = getCurrentUser();
        String messagingToken = currentUserInfo.getValue().getData().getMessagingToken();
        final UserInfo userInfo = new UserInfo(currentUser.getUid(), currentUser.getEmail(), firstname, lastname, address, gender, messagingToken);
        Database.updateUserInfo(userInfo).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                currentUserInfo.setValue(new DataWrapper<>(userInfo, UPDATED_USER_INFO_MESSAGE));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                currentUserInfo.setValue(new DataWrapper<UserInfo>(UPDATE_USER_INFO_ERROR));
            }
        });
    }

    public MutableLiveData<DataWrapper<UserInfo>> getCurrentUserInfo(){
        FirebaseUser user = getCurrentUser();
        if(user != null){
            Database.loadUserInfo(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                    currentUserInfo.setValue(new DataWrapper<>(userInfo));
                }
            });

        }
        return currentUserInfo;
    }
}
