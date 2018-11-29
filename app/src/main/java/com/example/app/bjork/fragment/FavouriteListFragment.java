package com.example.app.bjork.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.app.bjork.R;

public class FavouriteListFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // user unlogged = R.layout.fragment_user_unlogged
        // user has no favourite product = R.layout.fragment_empty_favourite_list
        // user has favourite products = R.layout.fragment_favourite_list

        View view = inflater.inflate(R.layout.fragment_user_unlogged, container, false);

        Button loginBtn = view.findViewById(R.id.loginButton);
        if(loginBtn != null){
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), getString(R.string.feature_not_available), Toast.LENGTH_LONG).show();
                }
            });
        }

        return view;
    }
}
