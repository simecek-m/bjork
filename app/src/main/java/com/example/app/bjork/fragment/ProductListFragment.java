package com.example.app.bjork.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.ProductsListAdapter;
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

    private List<Product> productsList = new ArrayList<>();

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProductsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerView = view.findViewById(R.id.productsList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ProductsListAdapter(getContext(), productsList);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        loadData();

        return view;
    }

    public void loadData(){
        Database database = new Database();
        DatabaseReference productsReference = database.getAllProducts();
        productsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot productsSnapshot) {
                for(DataSnapshot productSnapshot: productsSnapshot.getChildren()){
                    String id = productSnapshot.getKey();
                    Product product = productSnapshot.getValue(Product.class);
                    product.setId(id);
                    productsList.add(product);
                }
                adapter.setList(productsList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to load products");
            }
        });
    }
}


