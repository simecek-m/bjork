package com.example.app.bjork.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.app.bjork.R;
import com.example.app.bjork.model.Store;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NearestStoreActivity extends AppCompatActivity {

    private static final String TAG = "NearestStoreActivity";
    private static final String GOOGLE_MAPS_APP_PACKAGE = "com.google.android.apps.maps";
    private static final int MAP_ZOOM_LEVEL = 15;

    private Store store;
    private Button navigateButton;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_store);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("nearest_store")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        store = document.toObject(Store.class);
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                map = googleMap;
                                LatLng storeLocation = new LatLng(store.getLatitude(), store.getLongitude());
                                map.addMarker(new MarkerOptions().position(storeLocation).title(store.getName()));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(storeLocation, MAP_ZOOM_LEVEL));
                                if (isGoogleMapsAppInstalled()) {
                                    map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(LatLng latLng) {
                                            openGoogleMaps();
                                        }
                                    });
                                }
                            }
                        });

                        TextView storeName = findViewById(R.id.storeName);
                        storeName.setText(store.getName());
                        try {
                            String today = getTodayString();
                            String time = store.getOpeningTime().get(today);
                            TextView openingTime = findViewById(R.id.openingTime);
                            openingTime.setText(time);
                        } catch (NullPointerException e) {
                            Log.w(TAG, "Unavailable opening time", e);
                        }

                        ImageView storeImage = findViewById(R.id.storeImage);
                        Glide.with(getApplicationContext())
                                .load(store.getImageUrl())
                                .into(storeImage);
                    }
                });

        navigateButton = findViewById(R.id.navigationButton);
        if (!isGoogleMapsAppInstalled()) navigateButton.setVisibility(View.INVISIBLE);

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri intentUri = Uri.parse("google.navigation:q=" + store.getLatitude() + "," + store.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                mapIntent.setPackage(GOOGLE_MAPS_APP_PACKAGE);
                startActivity(mapIntent);
            }
        });
    }

    public String getTodayString(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        Date today = new Date();
        return sdf.format(today);
    }

    public boolean isGoogleMapsAppInstalled(){
        PackageManager packageManager = getPackageManager();
        try{
            packageManager.getPackageInfo(GOOGLE_MAPS_APP_PACKAGE, 0);
        }catch(PackageManager.NameNotFoundException e){
            return false;
        }
        return true;
    }

    public void openGoogleMaps(){
        float lat = store.getLatitude();
        float lon = store.getLongitude();
        String storeName = store.getName();
        Uri intentUri = Uri.parse("geo:0,0?q="+lat+","+lon+"("+storeName+")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        mapIntent.setPackage(GOOGLE_MAPS_APP_PACKAGE);
        startActivity(mapIntent);
    }
}
