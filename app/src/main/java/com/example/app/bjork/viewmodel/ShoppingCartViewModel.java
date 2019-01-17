package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.CartItem;
import com.example.app.bjork.wrapper.DataWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartViewModel extends ViewModel {

    public static final int DATA_UNAVAILABLE = 1;
    public static final int ITEM_NOT_REMOVED = 2;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private MutableLiveData<DataWrapper<List<CartItem>>> cartItemsList = new MutableLiveData<>();
    private MutableLiveData<Integer> totalPrice = new MutableLiveData<>();
    private MutableLiveData<DataWrapper<Pair<Integer, CartItem>>> deletedCartItem = new MutableLiveData<>();
    private MutableLiveData<Boolean> newOrder = new MutableLiveData<>();

    public MutableLiveData<DataWrapper<List<CartItem>>> getCartItemsList(){
        Database.getShoppingCart().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                List<Object> data = (List<Object>) httpsCallableResult.getData();
                List<CartItem> result = new ArrayList<>();
                for(Object obj: data) {
                    Gson gson = new Gson();
                    JsonElement element = gson.toJsonTree(obj);
                    CartItem item = gson.fromJson(element, CartItem.class);
                    result.add(item);
                }
                cartItemsList.setValue(new DataWrapper<>(result));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cartItemsList.setValue(new DataWrapper<List<CartItem>>(DATA_UNAVAILABLE));
            }
        });
        return cartItemsList;
    }

    public MutableLiveData<Integer> getTotalPrice() {
        int result = 0;
        if(cartItemsList.getValue() != null){
            for(CartItem item: cartItemsList.getValue().getData()){
                result += item.getPrice();
            }
            totalPrice.setValue(result);
        }
        return totalPrice;
    }

    public void removeItemFromCart(final Pair<Integer, CartItem> cartItem){
        Database.removeItemFromCart(auth.getUid(), cartItem.second.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deletedCartItem.setValue(new DataWrapper<>(cartItem));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deletedCartItem.setValue(new DataWrapper<Pair<Integer, CartItem>>(ITEM_NOT_REMOVED));
            }
        });
    }

    public MutableLiveData<DataWrapper<Pair<Integer, CartItem>>> getDeletedCartItem(){
        return deletedCartItem;
    }

    public void newOrder(){
        Database.newOrder().addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                newOrder.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                newOrder.setValue(false);
            }
        });
    }

    public MutableLiveData<Boolean> getNewOrder(){
        return newOrder;
    }

}
