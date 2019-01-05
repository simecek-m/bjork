package com.example.app.bjork.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.CompletionHandler;
import com.example.app.bjork.R;
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

    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchProducts(query);
        }

        text = findViewById(R.id.text);

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

                    text.setText(result.size() + " záznamů!");

//                        adapter.setList(result);
//                        adapter.notifyDataSetChanged();
                } catch (JSONException ex) {
                    Log.e(TAG, "requestCompleted: ", ex);
                }
            }
        });
    }
}
