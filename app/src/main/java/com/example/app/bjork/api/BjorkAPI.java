package com.example.app.bjork.api;

import com.example.app.bjork.model.Product;
import com.example.app.bjork.model.UserInfo;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class BjorkAPI {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void likeProduct(Product product){
        List<String> likes = product.getLikes();
        db.collection("products")
                .document(product.getId())
                .update("likes", likes);

    }

    public static Task<QuerySnapshot> loadProducts(){
        return db.collection("products")
                .get();
    }

    public static Task<QuerySnapshot> loadFavouritesProducts(String userId){
        return db.collection("products")
                .whereArrayContains("likes", userId)
                .get();
    }

    public static Task<DocumentSnapshot> loadUserInfo(String userId){
        return db.collection("user_info")
                .document(userId)
                .get();
    }

    public static void addUserInfo(UserInfo userInfo){
        db.collection("user_info")
                .document(userInfo.getId())
                .set(userInfo);
    }
}
