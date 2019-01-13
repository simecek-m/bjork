package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.Uri;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.Store;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class NearestStoreViewModel extends ViewModel{

    private MutableLiveData<Store> nearestStore = new MutableLiveData<>();

    public LiveData<Store> getNearestStore(){
        Database.getNearestStore().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                Store store = document.toObject(Store.class);
                nearestStore.setValue(store);
            }
        });
        return nearestStore;
    }

    public Uri getNearestStoreUri(){
        Store store = nearestStore.getValue();
        float lat = store.getLatitude();
        float lon = store.getLongitude();
        String storeName = store.getName();
        return Uri.parse("geo:0,0?q="+lat+","+lon+"("+storeName+")");
    }
}
