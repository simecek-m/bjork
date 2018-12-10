package com.example.app.bjork.api;

import com.example.app.bjork.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BjorkAPI {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void likeProduct(Product product){
        List<String> likes = product.getLikes();
        db.collection("products")
                .document(product.getId())
                .update("likes", likes);

    }
}
