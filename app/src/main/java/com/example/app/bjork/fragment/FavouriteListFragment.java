package com.example.app.bjork.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.bjork.R;
import com.google.firebase.auth.FirebaseAuth;

public class FavouriteListFragment extends Fragment {


    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourite_list, container, false);
        return view;
    }
}
