package com.example.app.bjork.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.app.bjork.R;
import com.example.app.bjork.viewmodel.LoginViewModel;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    @Email
    private EditText mEmail;

    @NotEmpty
    @Password(min = 8)
    private EditText mPassword;
    private Button mLoginBtn;

    private Validator validator;

    private BottomSheetDialog registrationBottomSheet;
    private View registrationButton;
    private View progressBar;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        showToolbar();

        validator = new Validator(this);
        validator.setValidationListener(this);

        createRegistrationBottomSheet();

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginButton);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    private void createRegistrationBottomSheet() {
        registrationBottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        registrationBottomSheet.setContentView(R.layout.registration_bottom_sheet);
        registrationBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                loginViewModel.resetLoginAttempt();
            }
        });
        registrationButton = registrationBottomSheet.findViewById(R.id.registration);
        progressBar = registrationBottomSheet.findViewById(R.id.progress_bar);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
            }
        });
    }

    public void login(){
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        loginViewModel.login(email, password);
    }

    public void registration(){
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        registrationButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        loginViewModel.registration(email, password);
    }

    @Override
    public void onValidationSucceeded() {
        login();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
        }
    }

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginViewModel.getLoginFinished().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer loginResult) {
                if(loginResult == LoginViewModel.LOGIN_USER_NOT_FOUND){
                    registrationBottomSheet.show();
                }else if(loginResult == LoginViewModel.LOGIN_SUCCESSFUL){
                    finish();
                }else if(loginResult == LoginViewModel.LOGIN_WRONG_CREDENTIALS){
                    mPassword.setError(getString(R.string.wrong_password));
                }
            }
        });
        loginViewModel.getRegistrationFinished().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer registrationResult) {
                if(registrationResult == LoginViewModel.REGISTRATION_COMPLETED){
                    registrationBottomSheet.dismiss();
                    login();
                }else if(registrationResult == LoginViewModel.REGISTRATION_FAILED){
                    registrationButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        loginViewModel.getRegistrationFinished().removeObservers(this);
        loginViewModel.getLoginFinished().removeObservers(this);
    }
}
