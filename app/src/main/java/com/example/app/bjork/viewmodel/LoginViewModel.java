package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginViewModel extends ViewModel {


    public static final int LOGIN_NO_ATTEMPT = 0;
    public static final int LOGIN_SUCCESSFUL = 1;
    public static final int LOGIN_WRONG_CREDENTIALS = 2;
    public static final int LOGIN_USER_NOT_FOUND = 3;
    public static final int REGISTRATION_COMPLETED = 1;
    public static final int REGISTRATION_FAILED = 2;

    private static final String TAG = "LoginViewModel";

    private MutableLiveData<Integer> loginFinished = new MutableLiveData<>();
    private MutableLiveData<Integer> registrationFinished = new MutableLiveData<>();

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void login(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(final AuthResult authResult) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        Database.updateMessagingToken(authResult.getUser().getUid(), token).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                loginFinished.setValue(LOGIN_SUCCESSFUL);
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidUserException) {
                    loginFinished.setValue(LOGIN_USER_NOT_FOUND);
                } else {
                    loginFinished.setValue(LOGIN_WRONG_CREDENTIALS);
                }
            }
        });
    }

    public void registration(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                UserInfo userInfo = new UserInfo();
                userInfo.setId(authResult.getUser().getUid());
                userInfo.setEmail(authResult.getUser().getEmail());
                Database.addUserInfo(userInfo).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        registrationFinished.setValue(REGISTRATION_COMPLETED);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        registrationFinished.setValue(REGISTRATION_FAILED);
                        Log.e(TAG, "onFailure: ", e);
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    public void resetLoginAttempt(){
        loginFinished.setValue(LOGIN_NO_ATTEMPT);
    }

    public MutableLiveData<Integer> getLoginFinished(){
        return loginFinished;
    }

    public MutableLiveData<Integer> getRegistrationFinished(){
        return registrationFinished;
    }
}
