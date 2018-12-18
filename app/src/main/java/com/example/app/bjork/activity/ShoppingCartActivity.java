package com.example.app.bjork.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.ShoppingCartAdapter;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.CartItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    private static final String TAG = "ShoppingCartActivity";
    
    private FirebaseAuth auth;
    private List<CartItem> list;

    private RecyclerView recyclerView;
    private ShoppingCartAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.cart);
        ab.setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        list = new ArrayList<>();

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.hasFixedSize();
        adapter = new ShoppingCartAdapter(this, list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        if(auth.getUid() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else{
            loadData();
        }
    }

    public int getPrice(){
        int result = 0;
        for(CartItem item: list){
            result += item.getPrice();
        }
        return result;
    }

    public void loadData(){
        BjorkAPI.getShoppingCart().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        List<Object> result = (List<Object>) httpsCallableResult.getData();
                        List<CartItem> items = new ArrayList<>();
                        for(Object obj: result) {
                            Gson gson = new Gson();
                            JsonElement element = gson.toJsonTree(obj);
                            CartItem item = gson.fromJson(element, CartItem.class);
                            items.add(item);
                        }
                        adapter.setList(items);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "load getShoppingCart failed: ", e);
                    }
                });
    }
}
