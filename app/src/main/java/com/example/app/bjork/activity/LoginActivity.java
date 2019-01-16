package com.example.app.bjork.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.bjork.R;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {


    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            finish();
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.login);

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
        final String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(final AuthResult authResult) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String token = instanceIdResult.getToken();
                        BjorkAPI.updateMessagingToken(authResult.getUser().getUid(), token);
                    }
                });
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                try{
                    throw e;
                }catch (FirebaseAuthInvalidUserException invalidUserException) {
                    registrationBottomSheet.show();
                }catch (FirebaseAuthInvalidCredentialsException ex){
                    mPassword.setError(getString(R.string.wrong_password));
                }catch (Exception ex){
                    Log.e(TAG, "onFailure: ", ex);
                }
            }
        });
    }

    public void registration(){
        registrationButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                UserInfo userInfo = new UserInfo();
                userInfo.setId(authResult.getUser().getUid());
                userInfo.setEmail(authResult.getUser().getEmail());
                BjorkAPI.addUserInfo(userInfo).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        registrationBottomSheet.dismiss();
                        login();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        registrationButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
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
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
