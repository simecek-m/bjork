package com.example.app.bjork.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.bjork.R;
import com.example.app.bjork.api.Database;
import com.example.app.bjork.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProductListFragment extends Fragment {

    private static final String TAG = "ProductListFragment";

    List<Product> productList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        Database database = new Database();
        DatabaseReference productsReference = database.getAllProducts();
        productsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot productsSnapshot) {
                for(DataSnapshot productSnapshot: productsSnapshot.getChildren()){
                    productList.add(productSnapshot.getValue(Product.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "Failed to load products");
            }
        });

        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }
}


