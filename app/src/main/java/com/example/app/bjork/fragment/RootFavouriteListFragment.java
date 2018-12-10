package com.example.app.bjork.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.bjork.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RootFavouriteListFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_root_favourite_list, container, false);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new UserUnloggedFragment())
                .commit();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkCorrectFragment();
    }

    public void checkCorrectFragment(){
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null ){
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new UserUnloggedFragment())
                    .commit();
        }else{
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new EmptyFavouriteListFragment())
                    .commit();
        }
    }
}
