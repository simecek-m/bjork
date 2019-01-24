package com.example.app.bjork.activity;

import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.app.bjork.R;
import com.example.app.bjork.adapter.SearchAdapter;
import com.example.app.bjork.animation.Animation;
import com.example.app.bjork.model.Product;
import com.example.app.bjork.viewmodel.SearchResultViewModel;
import com.example.app.bjork.wrapper.DataWrapper;

import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private SearchResultViewModel searchResultViewModel;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        searchResultViewModel = ViewModelProviders.of(this).get(SearchResultViewModel.class);
        showToolbar();
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new SearchAdapter(this);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        if(Intent.ACTION_SEARCH.equals(getIntent().getAction())){
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            searchResultViewModel.searchProducts(query);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        searchResultViewModel.getFoundProducts().observe(this, new Observer<DataWrapper<List<Product>>>() {
            @Override
            public void onChanged(@Nullable DataWrapper<List<Product>> productsDataWrapper) {
                if(productsDataWrapper.getError() == null){
                    View loadingAnimation = findViewById(R.id.loading_animation);
                    View emptyView = findViewById(R.id.empty_search);
                    RecyclerView recyclerView = findViewById(R.id.recycler_view);
                    List<Product> result = productsDataWrapper.getData();
                    adapter.setList(result);
                    if(result.size() == 0){
                        Animation.transitionViews(loadingAnimation, emptyView);
                    }else{
                        Animation.transitionViews(loadingAnimation, recyclerView);
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchResultViewModel.getFoundProducts().removeObservers(this);
    }

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.search);
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
