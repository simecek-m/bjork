package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.UserInfo;
import com.example.app.bjork.wrapper.DataWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class ProfileViewModel extends ViewModel {


    private static final String TAG = "ProfileViewModel";
    public static final int UPDATE_MESSAGING_TOKEN_ERROR = 1;

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

    public void updateUserInfo(UserInfo userInfo){
        Database.updateUserInfo(userInfo);
    }

    public MutableLiveData<DataWrapper<UserInfo>> getUserInfo() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            Database.loadUserInfo(currentUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(e != null){
                        Log.e(TAG, "onEvent: ", e);
                        return;
                    }

                    if(documentSnapshot != null && documentSnapshot.exists()){
                        currentUserInfo.setValue(new DataWrapper<>(documentSnapshot.toObject(UserInfo.class)));
                    }
                }
            });
        }else{
            currentUserInfo.setValue(null);
        }
        return currentUserInfo;
    }

}
