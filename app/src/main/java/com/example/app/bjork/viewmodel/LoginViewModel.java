package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<Boolean> loginFinished = new MutableLiveData<>();
    private MutableLiveData<Boolean> registrationFinished = new MutableLiveData<>();

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void login(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                loginFinished.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidUserException) {
                    loginFinished.setValue(null);
                } else {
                    loginFinished.setValue(false);
                }
            }
        });
    }

    public void registration(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                registrationFinished.setValue(true);
            }
        });
    }

    public MutableLiveData<Boolean> getLoginFinished(){
        return loginFinished;
    }

    public MutableLiveData<Boolean> getRegistrationFinished(){
        return registrationFinished;
    }
}
