package com.example.app.bjork.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.CompletionHandler;
import com.example.app.bjork.R;
import com.example.app.bjork.adapter.SearchAdapter;
import com.example.app.bjork.api.BjorkAPI;
import com.example.app.bjork.model.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultActivity";

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SearchAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);


        recyclerView = findViewById(R.id.recyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.search);
        ab.setDisplayHomeAsUpEnabled(true);


        adapter = new SearchAdapter();
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchProducts(query);
        }
    }

    public void searchProducts(String query){
        BjorkAPI.searchProducts(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                List<Product> result = new ArrayList<>();
                Gson gson = new GsonBuilder().create();
                try {
                    JSONArray array = jsonObject.getJSONArray("hits");
                    int arrayLength = array.length();
                    for (int i = 0; i < arrayLength; i++) {
                        String jsonString = array.get(i).toString();
                        Product product = gson.fromJson(jsonString, Product.class);
                        result.add(product);
                    }
                        adapter.setList(result);
                } catch (JSONException ex) {
                    Log.e(TAG, "requestCompleted: ", ex);
                }
            }
        });
    }
}
