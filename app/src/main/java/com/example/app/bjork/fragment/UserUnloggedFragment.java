package com.example.app.bjork.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.app.bjork.R;
import com.example.app.bjork.activity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class UserUnloggedFragment extends Fragment {


    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_unlogged, container, false);
        Button loginBtn = view.findViewById(R.id.loginButton);
        if(loginBtn != null){
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        return view;
    }
}