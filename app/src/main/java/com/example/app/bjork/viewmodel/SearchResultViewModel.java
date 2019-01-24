package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.CompletionHandler;
import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.Product;
import com.example.app.bjork.wrapper.DataWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultViewModel extends ViewModel {

    private MutableLiveData<DataWrapper<List<Product>>> foundProducts = new MutableLiveData<>();

    public static final int DATA_UNVAILABLE_ERROR = 1;


    public void searchProducts(String query){
        Database.searchProducts(query, new CompletionHandler() {
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
                    foundProducts.setValue(new DataWrapper<>(result));
                } catch (JSONException ex) {
                    foundProducts.setValue(new DataWrapper<List<Product>>(DATA_UNVAILABLE_ERROR));
                }
            }
        });
    }

    public MutableLiveData<DataWrapper<List<Product>>> getFoundProducts() {
        return foundProducts;
    }
}
