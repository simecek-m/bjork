package com.example.app.bjork.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.app.bjork.R;
import com.example.app.bjork.database.Database;
import com.example.app.bjork.constant.Constant;
import com.example.app.bjork.model.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText firstnameText;
    private EditText lastnameText;
    private EditText addressText;
    private Spinner genderSpinner;

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

            showToolbar();

            firstnameText = findViewById(R.id.firstnameText);
            lastnameText = findViewById(R.id.lastnameText);
            addressText = findViewById(R.id.addressText);
            genderSpinner = findViewById(R.id.gender);
            logoutButton = findViewById(R.id.logoutButton);
            saveUserInfoButton = findViewById(R.id.saveUserInfoButton);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Database.updateMessagingToken(auth.getUid(), null).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            auth.signOut();
                            finish();
                        }
                    });
                }
            });
            saveUserInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String firstname = firstnameText.getText().toString();
                    String lastname = lastnameText.getText().toString();
                    String address = addressText.getText().toString();
                    String gender = Constant.GENDERS[genderSpinner.getSelectedItemPosition()];
                    defaultUserInfo = new UserInfo(auth.getUid(), auth.getCurrentUser().getEmail() ,firstname, lastname, address, gender, defaultUserInfo.getMessagingToken());
                    Database.addUserInfo(defaultUserInfo);
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
        firstnameText.setInputType(InputType.TYPE_CLASS_TEXT);
        firstnameText.setFocusableInTouchMode(true);
        lastnameText.setInputType(InputType.TYPE_CLASS_TEXT);
        lastnameText.setFocusableInTouchMode(true);
        addressText.setInputType(InputType.TYPE_CLASS_TEXT);
        addressText.setFocusableInTouchMode(true);
        genderSpinner.setVisibility(View.VISIBLE);
        saveUserInfoButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.GONE);
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
        saveUserInfoButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.VISIBLE);
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
        Database.loadUserInfo(userId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        defaultUserInfo = documentSnapshot.toObject(UserInfo.class);
                        if(defaultUserInfo != null){
                            defaultUserInfo.setId(documentSnapshot.getId());
                            firstnameText.setText(defaultUserInfo.getFirstname());
                            lastnameText.setText(defaultUserInfo.getLastname());
                            addressText.setText(defaultUserInfo.getAddress());
                            if(defaultUserInfo.getGender() != null){
                                List<String> genders = Arrays.asList(Constant.GENDERS);
                                int selectedGenderIndex = genders.indexOf(defaultUserInfo.getGender());
                                genderSpinner.setSelection(selectedGenderIndex);
                            }
                        }
                        defaultRender();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(defaultState){
            super.onBackPressed();
        }else if(defaultUserInfo != null){
            firstnameText.setText(defaultUserInfo.getFirstname());
            lastnameText.setText(defaultUserInfo.getLastname());
            addressText.setText(defaultUserInfo.getAddress());
            List<String> genders = Arrays.asList(Constant.GENDERS);
            int selectedGenderIndex = genders.indexOf(defaultUserInfo.getGender());
            genderSpinner.setSelection(selectedGenderIndex);
        }else{
            genderSpinner.setSelection(0);
        }
        defaultRender();
    }

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.profile);
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
