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

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText addressText;
    private Spinner genderSpinner;
    private Button logoutButton;
    private Button updateUserInfoButton;

    private boolean defaultState = true;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        UserInfo userInfo = (UserInfo)getIntent().getSerializableExtra("currentUser");
        profileViewModel.setCurrentUserInfo(userInfo);
        final FirebaseUser currentUser = profileViewModel.getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_profile);
            showToolbar();
            firstNameText = findViewById(R.id.firstname_text);
            lastNameText = findViewById(R.id.lastname_text);
            addressText = findViewById(R.id.addressText);
            genderSpinner = findViewById(R.id.gender);
            logoutButton = findViewById(R.id.logout_user_button);
            updateUserInfoButton = findViewById(R.id.update_user_info_button);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileViewModel.logout();
                }
            });
            updateUserInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String firstName = firstNameText.getText().toString();
                    String lastName = lastNameText.getText().toString();
                    String address = addressText.getText().toString();
                    String gender = Constant.GENDERS[genderSpinner.getSelectedItemPosition()];
                    profileViewModel.updateUserInfo(firstName, lastName, address, gender);

                }
            });
            firstNameText.setOnClickListener(this);
            lastNameText.setOnClickListener(this);
            addressText.setOnClickListener(this);
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
                    String message = userInfoDataWrapper.getMesaage();
                    if(message != null && message.equals(profileViewModel.UPDATED_USER_INFO_MESSAGE)){
                        UserInfo updatedUserInfo = userInfoDataWrapper.getData();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("userInfo", updatedUserInfo);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }else{
                        renderDefaultState(userInfoDataWrapper.getData());
                    }
                }
            }
        });
    }

    public void renderEditUserState(){
        defaultState = false;
        firstNameText.setInputType(InputType.TYPE_CLASS_TEXT);
        firstNameText.setFocusableInTouchMode(true);
        lastNameText.setInputType(InputType.TYPE_CLASS_TEXT);
        lastNameText.setFocusableInTouchMode(true);
        addressText.setInputType(InputType.TYPE_CLASS_TEXT);
        addressText.setFocusableInTouchMode(true);
        genderSpinner.setVisibility(View.VISIBLE);
        updateUserInfoButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.GONE);
    }

    public void renderDefaultState(UserInfo userInfo){
        defaultState = true;
        firstNameText.setInputType(InputType.TYPE_NULL);
        firstNameText.setFocusable(false);
        firstNameText.setText(userInfo.getFirstname());
        lastNameText.setInputType(InputType.TYPE_NULL);
        lastNameText.setFocusable(false);
        lastNameText.setText(userInfo.getLastname());
        addressText.setInputType(InputType.TYPE_NULL);
        addressText.setFocusable(false);
        addressText.setText(userInfo.getAddress());
        genderSpinner.setVisibility(View.INVISIBLE);
        if(userInfo.getGender() != null){
            List<String> genders = Arrays.asList(Constant.GENDERS);
            int selectedGenderIndex = genders.indexOf(userInfo.getGender());
            genderSpinner.setSelection(selectedGenderIndex);
        }else{
            genderSpinner.setSelection(0);
        }
        updateUserInfoButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof EditText && defaultState) {
            renderEditUserState();
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
        }else{
            renderDefaultState(profileViewModel.getCurrentUserInfo().getValue().getData());
        }
    }

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.profile);
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
