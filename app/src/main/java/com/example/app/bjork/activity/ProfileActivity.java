package com.example.app.bjork.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
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
import com.example.app.bjork.constant.Constant;
import com.example.app.bjork.model.UserInfo;
import com.example.app.bjork.viewmodel.ProfileViewModel;
import com.example.app.bjork.wrapper.DataWrapper;
import com.google.firebase.auth.FirebaseUser;

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

    private ProfileViewModel profileViewModel;
    private UserInfo defaultUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        final FirebaseUser currentUser = profileViewModel.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_profile);
            showToolbar();
            firstnameText = findViewById(R.id.firstname_text);
            lastnameText = findViewById(R.id.lastname_text);
            addressText = findViewById(R.id.addressText);
            genderSpinner = findViewById(R.id.gender);
            logoutButton = findViewById(R.id.logout_user_button);
            saveUserInfoButton = findViewById(R.id.update_user_info_button);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileViewModel.logout();
                }
            });
            saveUserInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String firstname = firstnameText.getText().toString();
                    String lastname = lastnameText.getText().toString();
                    String address = addressText.getText().toString();
                    String gender = Constant.GENDERS[genderSpinner.getSelectedItemPosition()];
                    profileViewModel.updateUserInfo(firstname, lastname, address, gender);

                }
            });
            firstnameText.setOnClickListener(this);
            lastnameText.setOnClickListener(this);
            addressText.setOnClickListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        profileViewModel.getLoggedOut().observe(this, new Observer<DataWrapper<Boolean>>() {
            @Override
            public void onChanged(@Nullable DataWrapper<Boolean> userLoggedOutDataWrapper) {
                if(userLoggedOutDataWrapper.getError() == null && userLoggedOutDataWrapper.getData()){
                    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(ProfileActivity.this);
                    Intent intent = new Intent(Constant.BROADCAST_USER_LOG_OUT);
                    manager.sendBroadcast(intent);
                    finish();
                }
            }
        });
        profileViewModel.getCurrentUserInfo().observe(this, new Observer<DataWrapper<UserInfo>>() {
            @Override
            public void onChanged(@Nullable DataWrapper<UserInfo> userInfoDataWrapper) {
                if(userInfoDataWrapper.getError() == null){
                    String message = userInfoDataWrapper.getMesaage();
                    if(message != null && message.equals(ProfileViewModel.UPDATED_USER_INFO_MESSAGE)){
                        finish();
                    }else{
                        defaultUserInfo = userInfoDataWrapper.getData();
                        firstnameText.setText(defaultUserInfo.getFirstname());
                        lastnameText.setText(defaultUserInfo.getLastname());
                        addressText.setText(defaultUserInfo.getAddress());
                        if(defaultUserInfo.getGender() != null){
                            List<String> genders = Arrays.asList(Constant.GENDERS);
                            int selectedGenderIndex = genders.indexOf(defaultUserInfo.getGender());
                            genderSpinner.setSelection(selectedGenderIndex);
                        }
                        defaultRender();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        profileViewModel.getLoggedOut().removeObservers(this);
        profileViewModel.getCurrentUserInfo().removeObservers(this);
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
