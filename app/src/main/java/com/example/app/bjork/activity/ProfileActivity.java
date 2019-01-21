package com.example.app.bjork.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
        defaultUserInfo = (UserInfo)getIntent().getSerializableExtra("currentUser");
        profileViewModel.setCurrentUserInfo(defaultUserInfo);
        final FirebaseUser currentUser = profileViewModel.getCurrentUser();
        if(currentUser == null){
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
                    profileViewModel.changeUserInfo(firstname, lastname, address, gender);

                }
            });
            firstnameText.setOnClickListener(this);
            lastnameText.setOnClickListener(this);
            addressText.setOnClickListener(this);
            defaultUserInfo = (UserInfo)getIntent().getSerializableExtra("currentUser");
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

    @Override
    protected void onStart() {
        super.onStart();
        profileViewModel.getLoggedOff().observe(this, new Observer<DataWrapper<Boolean>>() {
            @Override
            public void onChanged(@Nullable DataWrapper<Boolean> userLoggedOffDataWrapper) {
                if(userLoggedOffDataWrapper.getError() == null && userLoggedOffDataWrapper.getData()){
                    finish();
                }
            }
        });
        profileViewModel.getCurrentUserInfo().observe(this, new Observer<DataWrapper<UserInfo>>() {
            @Override
            public void onChanged(@Nullable DataWrapper<UserInfo> userInfoDataWrapper) {
                if(userInfoDataWrapper.getError() == null){
                    defaultUserInfo = userInfoDataWrapper.getData();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("userInfo", defaultUserInfo);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
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
