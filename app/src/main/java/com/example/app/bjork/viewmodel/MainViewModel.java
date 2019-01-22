package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.UserInfo;
import com.example.app.bjork.wrapper.DataWrapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";
    private MutableLiveData<DataWrapper<UserInfo>> currentUserInfo = new MutableLiveData<>();

    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
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
