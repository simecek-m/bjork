package com.example.app.bjork.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.model.Store;
import com.example.app.bjork.viewmodel.NearestStoreViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearestStoreActivity extends AppCompatActivity {

    private static final int MAP_ZOOM_LEVEL = 15;

    private NearestStoreViewModel nearestStoreViewModel;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_store);
        nearestStoreViewModel = ViewModelProviders.of(this).get(NearestStoreViewModel.class);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        showToolbar();

        Button navigateButton = findViewById(R.id.navigationButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNavigation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        nearestStoreViewModel.getNearestStore().observe(this, new Observer<Store>() {
            @Override
            public void onChanged(@Nullable final Store store) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng storeLocation = new LatLng(store.getLatitude(), store.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(storeLocation).title(store.getName()));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, MAP_ZOOM_LEVEL));
                    }
                });
                TextView storeName = findViewById(R.id.storeName);
                storeName.setText(store.getName());

                TextView openingTime = findViewById(R.id.openingTime);
                openingTime.setText(store.getOpeningTimeToday());

                ImageView storeImage = findViewById(R.id.storeImage);
                Glide.with(getApplicationContext())
                        .load(store.getImageUrl())
                        .into(storeImage);

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        nearestStoreViewModel.getNearestStore().removeObservers(this);
    }

    public void openNavigation(){
        Uri uri = nearestStoreViewModel.getNearestStoreUri();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        Intent chooser = Intent.createChooser(mapIntent, getString(R.string.navigate));
        startActivity(chooser);
    }

    public void showToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.nearest_store);
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
