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

public class EmptyFavouriteListFragment extends Fragment {


    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_empty_favourite_list, container, false);

        return view;
    }
}
