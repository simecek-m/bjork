package com.example.app.bjork.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.app.bjork.database.Database;
import com.example.app.bjork.model.CartItem;
import com.example.app.bjork.model.UserInfo;
import com.example.app.bjork.wrapper.DataWrapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoppingCartViewModel extends ViewModel {

    public static final int DATA_UNAVAILABLE_ERROR = 1;
    public static final int NO_ITEM_WAS_REMOVED_ERROR = 2;
    public static final int NO_ITEM_WAS_RESTORED_ERROR = 3;

    public static final String REMOVED_ITEM_MESSAGE = "item removed";
    public static final String RESTORED_ITEM_MESSAGE = "item restored";

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private MutableLiveData<DataWrapper<List<CartItem>>> cartItemsList = new MutableLiveData<>();
    private MutableLiveData<Integer> totalPrice = new MutableLiveData<>();
    private MutableLiveData<Boolean> newOrder = new MutableLiveData<>();
    private MutableLiveData<CartItem> deletedCartItem = new MutableLiveData<>();

    public FirebaseUser getCurrentUser(){
        return  auth.getCurrentUser();
    }

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
                Collections.sort(result);
                cartItemsList.setValue(new DataWrapper<>(result));
                calculateTotalPrice(result);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cartItemsList.setValue(new DataWrapper<List<CartItem>>(DATA_UNAVAILABLE_ERROR));
            }
        });
        return cartItemsList;
    }

    public MutableLiveData<Integer> getTotalPrice() {
        return totalPrice;
    }

    private void calculateTotalPrice(List<CartItem> list){
        int result = 0;
        for(CartItem item: list){
            result += item.getPrice();
        }
        totalPrice.setValue(result);
    }

    public void deleteCartItem(final CartItem cartItem){
        Database.deleteCartItem(auth.getUid(), cartItem.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            public void onSuccess(Void aVoid) {
                List<CartItem> updatedList = cartItemsList.getValue().getData();
                updatedList.remove(cartItem);
                deletedCartItem.setValue(cartItem);
                totalPrice.setValue(totalPrice.getValue() - cartItem.getPrice());
                cartItemsList.setValue(new DataWrapper<>(updatedList, REMOVED_ITEM_MESSAGE));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                cartItemsList.setValue(new DataWrapper<List<CartItem>>(NO_ITEM_WAS_REMOVED_ERROR));
            }
        });
    }

    public void setNewOrder(){
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

    public void restoreDeletedCartItem(){
        final CartItem cartItem = deletedCartItem.getValue();
        if(cartItem != null){
            Database.restoreCartItem(auth.getUid(), cartItem).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    List<CartItem> updatedList = cartItemsList.getValue().getData();
                    updatedList.add(cartItem);
                    Collections.sort(updatedList);
                    totalPrice.setValue(totalPrice.getValue() + cartItem.getPrice());
                    cartItemsList.setValue(new DataWrapper<>(updatedList, RESTORED_ITEM_MESSAGE));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    cartItemsList.setValue(new DataWrapper<List<CartItem>>(NO_ITEM_WAS_RESTORED_ERROR));
                }
            });
        }
    }

    public CartItem getDeletedCartItem(){
        return deletedCartItem.getValue();
    }
}
