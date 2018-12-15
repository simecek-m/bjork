package com.example.app.bjork.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.app.bjork.R;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText firstnameText;
    private EditText lastnameText;
    private EditText addressText;
    private Spinner genderSpinner;
    private ConstraintLayout error;

    private Button logoutButton;
    private Button saveUserInfoButton;
    private boolean defaultState = true;
    private FirebaseAuth auth;

    private UserInfo defaultUserInfo;
    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultIntent = new Intent();
        auth = FirebaseAuth.getInstance();
        if(auth.getUid() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_profile);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar ab = getSupportActionBar();
            ab.setDisplayHomeAsUpEnabled(true);

            firstnameText = findViewById(R.id.firstnameText);
            lastnameText = findViewById(R.id.lastnameText);
            addressText = findViewById(R.id.addressText);
            genderSpinner = findViewById(R.id.gender);
            logoutButton = findViewById(R.id.logoutButton);
            saveUserInfoButton = findViewById(R.id.saveUserInfoButton);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    finish();
                }
            });
            error = findViewById(R.id.error);
            error.setVisibility(View.GONE);
            saveUserInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String firstname = firstnameText.getText().toString();
                    String lastname = lastnameText.getText().toString();
                    String address = addressText.getText().toString();
                    String gender = genderSpinner.getSelectedItem().toString();

                    defaultUserInfo = new UserInfo(auth.getUid(), auth.getCurrentUser().getEmail() ,firstname, lastname, address, gender);
                    BjorkAPI.addUserInfo(defaultUserInfo);
                    resultIntent.putExtra("userInfo", defaultUserInfo);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            });
            firstnameText.setOnClickListener(this);
            lastnameText.setOnClickListener(this);
            addressText.setOnClickListener(this);
            defaultRender();
            loadUserInfo(auth.getUid());
        }
    }

    public void editUser(){
        defaultState = false;
        error.setVisibility(View.GONE);
        firstnameText.setInputType(InputType.TYPE_CLASS_TEXT);
        firstnameText.setFocusableInTouchMode(true);
        lastnameText.setInputType(InputType.TYPE_CLASS_TEXT);
        lastnameText.setFocusableInTouchMode(true);
        addressText.setInputType(InputType.TYPE_CLASS_TEXT);
        addressText.setFocusableInTouchMode(true);
        genderSpinner.setVisibility(View.VISIBLE);
        saveUserInfoButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
    }

    public void defaultRender(){
        defaultState = true;
        firstnameText.setInputType(InputType.TYPE_NULL);
        firstnameText.setFocusable(false);
        lastnameText.setInputType(InputType.TYPE_NULL);
        lastnameText.setFocusable(false);
        addressText.setInputType(InputType.TYPE_NULL);
        addressText.setFocusable(false);
        genderSpinner.setVisibility(View.INVISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
        saveUserInfoButton.setVisibility(View.GONE);
        if(isProfileIncomplete()){
            error.setVisibility(View.VISIBLE);
        }else{
            error.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof EditText && defaultState) {
            editUser();
            EditText focusText = (EditText) v;
            focusText.requestFocus();
            focusText.setSelection(focusText.getText().length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(focusText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public boolean isProfileIncomplete(){
        return firstnameText.getText().toString().matches("") ||
                lastnameText.getText().toString().matches("") ||
                addressText.getText().toString().matches("");
    }

    public void loadUserInfo(final String userId){
        BjorkAPI.loadUserInfo(userId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        defaultUserInfo = documentSnapshot.toObject(UserInfo.class);
                        defaultUserInfo.setId(documentSnapshot.getId());
                        firstnameText.setText(defaultUserInfo.getFirstname());
                        lastnameText.setText(defaultUserInfo.getLastname());
                        addressText.setText(defaultUserInfo.getAddress());
                        defaultRender();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(defaultState){
            super.onBackPressed();
        }else{
            firstnameText.setText(defaultUserInfo.getFirstname());
            lastnameText.setText(defaultUserInfo.getLastname());
            addressText.setText(defaultUserInfo.getAddress());
            defaultRender();
        }
    }
}
